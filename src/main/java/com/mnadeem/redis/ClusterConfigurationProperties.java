package com.mnadeem.redis;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class ClusterConfigurationProperties {

	/*
	 * spring.redis.cluster.nodes[0] = 127.0.0.1:7379 spring.redis.cluster.nodes[1]
	 * = 127.0.0.1:7380 ...
	 */
	private List<String> nodes;
	/**
	 * spring.redis.cluster.max-redirects=3
	 */
	private int maxRedirects;

	/**
	 * Get initial collection of known cluster nodes in format {@code host:port}.
	 *
	 * @return
	 */
	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}
	
}
