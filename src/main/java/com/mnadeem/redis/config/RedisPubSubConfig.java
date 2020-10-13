package com.mnadeem.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.mnadeem.redis.service.pubsub.MessagePublisher;
import com.mnadeem.redis.service.pubsub.RedisMessagePublisher;
import com.mnadeem.redis.service.pubsub.RedisMessageSubscriber;

@Configuration
public class RedisPubSubConfig {

	@Bean
	MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new RedisMessageSubscriber());
	}

	@Bean
	RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory ) {
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(messageListener(), topic());
		return container;
	}

	@Bean
	MessagePublisher redisPublisher(RedisTemplate<String, String> redisTemplate) {
		return new RedisMessagePublisher(redisTemplate, topic());
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("pubsub:topic");
	}
}
