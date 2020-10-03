package com.mnadeem.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mnadeem.redis.service.RedisService;

@SpringBootApplication
@EnableAsync
public class SpringBootRedisApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootRedisApplication.class);
	
	@Autowired
	private RedisService redisService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRedisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		setProducerConsumer();
	}

	private void setProducerConsumer() throws InterruptedException {
		redisService.writeSet();
		redisService.readSet();
		redisService.readSet();		
		LOGGER.info("Set Producer Consumer");
	}
}
