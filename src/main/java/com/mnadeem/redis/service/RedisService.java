package com.mnadeem.redis.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOps;

	@Async
	public void readSet() throws InterruptedException {
		while (true) {
			TimeUnit.MICROSECONDS.sleep(1);
			String value = setOps.pop("setKey");
			if (value == null) {
				TimeUnit.MICROSECONDS.sleep(1);
			} else {
				LOGGER.info(value);
			}
		}
	}

	@Async
	public void writeSet() throws InterruptedException {
		int i = 0;
		while (true) {
			TimeUnit.MICROSECONDS.sleep(1);
			if (i == 10) {
				break;
			}
			setOps.add("setKey", String.valueOf(i));
			i++;
		}
	}
}
