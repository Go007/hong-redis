package com.hong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedisClusterParam {
	
	private Map<String, List<String>> cluster;
	private String password;
	public Map<String, List<String>> getCluster() {
		return cluster;
	}
	public void setCluster(Map<String, List<String>> cluster) {
		this.cluster = cluster;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}