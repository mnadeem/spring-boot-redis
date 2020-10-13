package com.mnadeem.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mnadeem.redis.service.RedisService;
import com.mnadeem.redis.service.pubsub.RedisMessagePublisher;

@SpringBootApplication
@EnableAsync
public class SpringBootRedisApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootRedisApplication.class);
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private RedisMessagePublisher messagePublisher;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRedisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//setProducerConsumer();
		//setPubSubBlockingWait();
		//expire();
		//listProducerConsumer();
		publish();
	}

	private void setProducerConsumer() throws InterruptedException {
		LOGGER.info("Set Producer Consumer");
		String key = "setKey";
		redisService.writeSet(key);
		redisService.readSet(key);
		redisService.readSet(key);		
		
	}
	
	private void setPubSubBlockingWait() throws InterruptedException {
		//https://redis.io/commands/blpop
		LOGGER.info("Set Producer Consumer");

		String setKey = "setKeyB";
		String blockingKey = "setKeyLB";
		redisService.writeSetBlocking(setKey, blockingKey);
		redisService.readSetBlocking(setKey, blockingKey);
		redisService.readSetBlocking(setKey, blockingKey);
	}
	
	private void expire() throws InterruptedException {
		redisService.valExpire("valExpireKey");
		redisService.setExpire("setExpireKey");
		redisService.listExpire("listExpireKey");
	}
	
	private void listProducerConsumer() throws InterruptedException {
		LOGGER.info("List Producer Consumer");
		String key = "listKey";
		redisService.readList(key);
		redisService.readList(key);
		redisService.writeList(key);
	}
	
	private void publish() throws InterruptedException {
		while(true) {
			int i = 0;
			while (true) {
				TimeUnit.MINUTES.sleep(10);
				if (i == 10) {
					break;
				}
				messagePublisher.publish(String.valueOf(i));
				LOGGER.info("Published {}", i);
				i++;
			}
		}
	}
}
