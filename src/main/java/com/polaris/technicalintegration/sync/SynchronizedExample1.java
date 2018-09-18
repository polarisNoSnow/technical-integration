package com.polaris.technicalintegration.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedExample1 {
	/*同步块:作用于调用的对象*/
	public void test1(String m) {
		synchronized(this) {
			for(int i = 1; i < 10; i++) {
				log.info("test1-{}-{}",m,i);
			}
		}
		
	}
	/*同步方法:作用于调用的对象*/
	public synchronized void test2(String m) {
		for(int i = 1; i < 10; i++) {
			log.info("test1-{}-{}",m,i);
		}
		
	}
	
	public static void main(String[] args) {
		SynchronizedExample1 example1 = new SynchronizedExample1();
		SynchronizedExample1 example2= new SynchronizedExample1();
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(()->{
			example1.test2("A");
		});
		executorService.execute(()->{
			example1.test2("B");
		});
		//不同对象 互相不影响
		executorService.execute(()->{
			example2.test2("C");
		});
	}
}
