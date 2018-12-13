package com.hong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.redisson")
@Configuration
public class RedissonParam {
	
	private boolean iscluster;//: true
	private Integer scanInterval;//: 1000
	private Integer failedAttempts;//: 3
	private Integer reconnectionTimeout;//: 3000
	private Integer retryInterval;//: 1500
	private Integer retryAttempts;//: 3
	private Integer timeout;//: 3000
	private Integer connectTimeout;//: 10000
	private Integer idleConnectionTimeout;//: 10000
	private Integer masterConnectionPoolSize;//: 64
	private Integer slaveConnectionMinimumIdleSize;//: 10
	
	public boolean isIscluster() {
		return iscluster;
	}
	public void setIscluster(boolean iscluster) {
		this.iscluster = iscluster;
	}
	public Integer getScanInterval() {
		return scanInterval;
	}
	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}
	public Integer getFailedAttempts() {
		return failedAttempts;
	}
	public void setFailedAttempts(Integer failedAttempts) {
		this.failedAttempts = failedAttempts;
	}
	public Integer getReconnectionTimeout() {
		return reconnectionTimeout;
	}
	public void setReconnectionTimeout(Integer reconnectionTimeout) {
		this.reconnectionTimeout = reconnectionTimeout;
	}
	public Integer getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(Integer retryInterval) {
		this.retryInterval = retryInterval;
	}
	public Integer getRetryAttempts() {
		return retryAttempts;
	}
	public void setRetryAttempts(Integer retryAttempts) {
		this.retryAttempts = retryAttempts;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public Integer getIdleConnectionTimeout() {
		return idleConnectionTimeout;
	}
	public void setIdleConnectionTimeout(Integer idleConnectionTimeout) {
		this.idleConnectionTimeout = idleConnectionTimeout;
	}
	public Integer getMasterConnectionPoolSize() {
		return masterConnectionPoolSize;
	}
	public void setMasterConnectionPoolSize(Integer masterConnectionPoolSize) {
		this.masterConnectionPoolSize = masterConnectionPoolSize;
	}
	public Integer getSlaveConnectionMinimumIdleSize() {
		return slaveConnectionMinimumIdleSize;
	}
	public void setSlaveConnectionMinimumIdleSize(Integer slaveConnectionMinimumIdleSize) {
		this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
	}
}