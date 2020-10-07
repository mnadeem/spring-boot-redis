package com.mnadeem.redis.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOps;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Async
	public void readSet(String key) throws InterruptedException {
		while (true) {
			TimeUnit.MICROSECONDS.sleep(1);
			String value = setOps.pop(key);
			if (value == null) {
				TimeUnit.MICROSECONDS.sleep(1);
			} else {
				LOGGER.info(value);
			}
		}
	}

	@Async
	public void writeSet(String key) throws InterruptedException {
		int i = 0;
		while (true) {
			TimeUnit.MICROSECONDS.sleep(1);
			if (i == 10) {
				break;
			}
			setOps.add(key, String.valueOf(i));
			i++;
		}
	}

	@Async
	public void readSetBlocking(String setKey, String blockingKey) throws InterruptedException {
		
		while (true) {
			TimeUnit.MICROSECONDS.sleep(100);
			String value = setOps.pop(setKey);
			while (value != null) {
				TimeUnit.MICROSECONDS.sleep(100);
				LOGGER.info(value);
				value = setOps.pop(setKey);
			}
			stringRedisTemplate.opsForList().rightPop(blockingKey, Duration.ZERO);	
		}
	}

	@Async
	public void writeSetBlocking(String setKey, String blockingKey) throws InterruptedException {
		final AtomicInteger i = new AtomicInteger(0);
		while (true) {
			TimeUnit.SECONDS.sleep(1);
			if (i.get() == 10) {
				break;
			}
			//https://redis.io/topics/transactions
			stringRedisTemplate.execute(new SessionCallback<Object>() {

				@SuppressWarnings("unchecked")
				@Override
				public Object execute(RedisOperations operations) throws DataAccessException {
					do {
						operations.watch(setKey);
						
						operations.multi();
						operations.opsForSet().add(setKey, String.valueOf(i.incrementAndGet()));
						operations.opsForList().leftPush(blockingKey, "");						
					} while (operations.exec() == null);
					return null;
				}
			});
		}
	}

	@Async
	public List<String> executePipelined(String... ids) {
		final List<String> entries = new ArrayList<>();
		stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
			@Override 
			public Object doInRedis(RedisConnection connection) throws DataAccessException { 
				for(String id : ids) {
					connection.get(id.getBytes());
					// Some other operation utilizing connection
				}		 
				return null; 
			}
		}); 
		return entries; 
	}
	
	@Async
	public void valExpire(String key) throws InterruptedException {
		stringRedisTemplate.opsForValue().set(key, "SomeValue", Duration.ofSeconds(1));
		TimeUnit.MILLISECONDS.sleep(100);
		LOGGER.info("After 100 micro secs, Value for " + key + " : " + stringRedisTemplate.opsForValue().get(key));
		
		TimeUnit.SECONDS.sleep(1);
		LOGGER.info("After 1s, Value for " + key + " : "  + stringRedisTemplate.opsForValue().get(key));
	}

	@Async
	public void listExpire(String key) throws InterruptedException {
		BoundListOperations<String, String> boundOperations = stringRedisTemplate.boundListOps(key);
		boundOperations.leftPush("SomeValue");
		boundOperations.expire(Duration.ofSeconds(1));

		TimeUnit.MILLISECONDS.sleep(100);
		//LOGGER.info("After 100 micro secs, Size for " + key + " : " + stringRedisTemplate.opsForList().size(key));
		LOGGER.info("After 100 micro secs, Size for " + key + " : " + boundOperations.size());
		
		TimeUnit.SECONDS.sleep(1);
		//LOGGER.info("After 1s, Size for " + key + " : "  + stringRedisTemplate.opsForList().size(key));
		LOGGER.info("After 1s, Size for " + key + " : "  + boundOperations.size());
	}

	@Async
	public void setExpire(String key) throws InterruptedException {

		stringRedisTemplate.execute(new SessionCallback<Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				do {
					operations.multi();
					BoundSetOperations<String, String> boundOperations = operations.boundSetOps(key);
					boundOperations.add("Some value");
					boundOperations.expire(Duration.ofSeconds(1));
				} while (operations.exec() == null);
				return null;
			}
		});

		BoundSetOperations<String, String> boundOperations = stringRedisTemplate.boundSetOps(key);

		TimeUnit.MILLISECONDS.sleep(100);
		LOGGER.info("After 100 micro secs, Size for " + key + " : " + boundOperations.size());
		
		TimeUnit.SECONDS.sleep(1);
		LOGGER.info("After 1s, Size for " + key + " : "  + boundOperations.size());
	}
}
