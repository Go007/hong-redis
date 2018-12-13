package com.hong.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RedissonConnector {
	
	private static final String CLUSTER_NODES = "nodes";
	private static final String REDIS_PREFIX = "redis://";
	private static final String COLON_SYMBOL = ":";
	
	RedissonClient redisson;
	
	@Autowired
	private RedissonParam redissonParam;
	@Autowired
	private RedisClusterParam redisClusterParam;
	@Autowired
	private RedisSingleParam redisSingleParam;

	@PostConstruct
	public void init() {
		Config config = new Config();
		if(redissonParam.isIscluster()) {
			ClusterServersConfig useClusterServers = config.useClusterServers();//集群扫描间隔时间
			List<String> cluster = redisClusterParam.getCluster().get(CLUSTER_NODES);
			for(String str :cluster) {
				useClusterServers.addNodeAddress(REDIS_PREFIX+str);
			}
			if(StringUtils.isNoneBlank(redisClusterParam.getPassword())) {
				useClusterServers.setPassword(redisClusterParam.getPassword());
			}
			useClusterServers.setScanInterval(redissonParam.getScanInterval())
							.setFailedAttempts(redissonParam.getFailedAttempts())
							.setReconnectionTimeout(redissonParam.getReconnectionTimeout())
							.setRetryAttempts(redissonParam.getRetryAttempts())
							.setReconnectionTimeout(redissonParam.getReconnectionTimeout())
							.setRetryAttempts(redissonParam.getRetryAttempts())
							.setRetryInterval(redissonParam.getRetryInterval())
							.setTimeout(redissonParam.getTimeout())
							.setConnectTimeout(redissonParam.getConnectTimeout())
							.setIdleConnectionTimeout(redissonParam.getIdleConnectionTimeout())
							.setMasterConnectionPoolSize(redissonParam.getMasterConnectionPoolSize())
							.setSlaveConnectionMinimumIdleSize(redissonParam.getSlaveConnectionMinimumIdleSize());
		} else {
			SingleServerConfig useSingleServer = config.useSingleServer();
			useSingleServer.setAddress(REDIS_PREFIX+redisSingleParam.getHost()+COLON_SYMBOL+redisSingleParam.getPort());
			if(StringUtils.isNoneBlank(redisSingleParam.getPassword())) {
				useSingleServer.setPassword(redisSingleParam.getPassword());
			}
			useSingleServer.setFailedAttempts(redissonParam.getFailedAttempts())
							.setReconnectionTimeout(redissonParam.getReconnectionTimeout())
							.setRetryInterval(redissonParam.getRetryInterval())
							.setRetryAttempts(redissonParam.getRetryAttempts())
							.setTimeout(redissonParam.getTimeout())
							.setConnectTimeout(redissonParam.getConnectTimeout())
							.setIdleConnectionTimeout(redissonParam.getIdleConnectionTimeout());
		}
		redisson = Redisson.create(config);
	}
	
	public RedissonClient getClient() {
		return redisson;
	}
	
}
