package com.polaris.technicalintegration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.polaris.technicalintegration.annotation.RateLimiter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TechnicalIntegrationApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired 
    private Service service;
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void test() {
		System.out.println(System.currentTimeMillis());
		for (int i = 0; i < 200; i++) {
			service.doPress("polaris");
		}
		
	}
	
	
}
