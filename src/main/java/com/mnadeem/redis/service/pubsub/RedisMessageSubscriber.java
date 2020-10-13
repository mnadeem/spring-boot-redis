package com.mnadeem.redis.service.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageSubscriber.class);

    public void onMessage(final Message message, final byte[] pattern) {
    	LOGGER.info("Message received: {}", message.toString());
    }
}