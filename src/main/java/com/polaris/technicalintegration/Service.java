package com.polaris.technicalintegration;


import com.polaris.technicalintegration.annotation.RateLimiter;

@org.springframework.stereotype.Service
public class Service {
	
	@RateLimiter(limit = 100, timeout = 1, bizRemark = "限流测试接口")
	public void doPress(String param) {
		System.out.println("result:"+param);
	}
}
