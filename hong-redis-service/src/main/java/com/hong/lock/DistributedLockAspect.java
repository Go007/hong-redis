package com.hong.lock;

import com.hong.annotations.LockAction;
import com.hong.annotations.RedisParameterLocked;
import com.hong.common.bean.Result;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 *  RedisLock AOP
 */
@Aspect
@Configuration
@ConditionalOnClass(DistributedLock.class)
public class DistributedLockAspect {
	
	private final Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);
	
	@Autowired
	private DistributedLock distributedLock;
	
	private ExpressionParser parser = new SpelExpressionParser();

	@Autowired
	private KeyGenerator keyGenerator;

	/**
	 * 获取方法的参数名
	 */
	private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

	/**
	 * 针对类的切点使用@within ， @annotation是针对方法的
	 */
/*	@Pointcut("@annotation(com.hong.annotations.LockAction)")
	private void lockPoint(){ }
	
	@Around("lockPoint()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable{*/
	@Around("@annotation(lockAction)")
	public Object around(ProceedingJoinPoint pjp,LockAction lockAction) throws Throwable{
		/**
		 * Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		 * TODO 这种方式当目标类存在接口时,获取不到目标类方法上的注解,没搞明白啥原因?
		 * LockAction lockAction = method.getAnnotation(LockAction.class);
		 */
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		String key = lockAction.value();
		Object[] args = pjp.getArgs();
		// 如果不特别指定key,则自动生成key
		if (StringUtils.isBlank(key)){
			key = keyGenerator.generate(pjp.getTarget(),method,args).toString();
		}else {
			//key = parse(key, method, args);
			Annotation[][] annotations = method.getParameterAnnotations();
			key += getLockedObject(annotations, args);
		}

		int retryTimes = lockAction.action().equals(LockAction.LockFailAction.CONTINUE) ? lockAction.retryTimes() : 0;
		boolean lock = distributedLock.lock(key, lockAction.keepMills(), retryTimes, lockAction.sleepMills());
		if(!lock) {
			logger.debug("get lock failed : " + key);
			/**
			 * 这里要注意返回的结构要遵循外层调用接口声明的返回值,可以统一接口的返回结构为Result
			 */
			return Result.buildFailed(lockAction.msg());
		}
		
		//得到锁,执行方法，释放锁
		logger.debug("get lock success : " + key);
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (Exception e) {
			logger.error("execute locked method occured an exception", e);
			throw e;
		}

		boolean isManualReleaseLock = lockAction.isManualReleaseLock();
		// 手动触发释放锁
		if (isManualReleaseLock){
			boolean releaseResult = distributedLock.releaseLock(key);
			logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
		}

		return result;
	}
	
	/**
	 * @description 解析spring EL表达式
	 * @version 1.0.0
	 * @param key 表达式
	 * @param method 方法
	 * @param args 方法参数
	 * @return
	 */
	private String parse(String key, Method method, Object[] args) {
		String[] params = discoverer.getParameterNames(method);
		// Expression是从指定的上下文(EvaluationContext)中获取相应的内容
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < params.length; i ++) {
			context.setVariable(params[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, String.class);
	}

	/**
	 * 锁细粒度控制，获取方法锁参数
	 * @param annotations
	 * @param args
	 * @return
	 * @throws NoSuchFieldException
     */
	private String getLockedObject(Annotation[][] annotations, Object[] args)
			throws NoSuchFieldException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations[i].length; j++) {
				if (annotations[i][j] instanceof RedisParameterLocked) {
					sb.append(args[i].toString());
				}
			}
		}
		return sb.toString();
	}
}
