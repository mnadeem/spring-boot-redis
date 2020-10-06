package com.mnadeem.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ReadFrom;

@Configuration
public class RedisConfig {

	@Autowired
	private ClusterConfigurationProperties clusterProperties;

	@Bean
	LettuceConnectionFactory redisConnectionFactory(RedisClusterConfiguration redisConfiguration) {

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.readFrom(ReadFrom.REPLICA_PREFERRED).build();

		return new LettuceConnectionFactory(redisConfiguration, clientConfig);
	}

	@Bean
	RedisClusterConfiguration redisConfiguration() {
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterProperties.getNodes());
		redisClusterConfiguration.setMaxRedirects(clusterProperties.getMaxRedirects());

		return redisClusterConfiguration;
	}

	@Bean	
	@Qualifier("objectRedisTemplate")
	RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		// other settings...
		return template;
	}

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	@Primary
	StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(StringRedisSerializer.UTF_8);
		template.setValueSerializer(StringRedisSerializer.UTF_8);
		// other settings...
		return template;
	}
}
