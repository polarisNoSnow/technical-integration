package com.polaris.technicalintegration.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedExample2 {
	/*同步块:作用于调用的对象*/
	public static synchronized void test1(String m) {
		for(int i = 1; i < 10; i++) {
			log.info("test1-{}-{}",m,i);
		}
	
	}
	/*同步方法:作用于调用的对象*/
	public static void test2(int m) {
		synchronized(SynchronizedExample2.class) {
			for(int i = 1; i < 10; i++) {
				log.info("test1-{}-{}",m,i);
			}
		}
	}
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(()->{
			SynchronizedExample2.test1("A");
		});
		executorService.execute(()->{
			SynchronizedExample2.test1("B");
		});
		//不同对象 互相不影响
		executorService.execute(()->{
			SynchronizedExample2.test1("C");
		});
	}
}
