package com.polaris.technicalintegration.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.technicalintegration.annotation.ThreadSafe;

import lombok.extern.slf4j.Slf4j;

/**
 * AtomicReference相关，重要性低
 * AtomicStampedReference采用版本号更新，解决CAS的ABA问题
 * @author polaris
 * @date 2018年9月5日
 */
@Slf4j
@ThreadSafe
public class AtomicTest4 {
	/*模拟客户端数*/
	private static int clientTotal = 200000;
	/*并发线程数据*/
	private static int threadTotal = 200;
	
	private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
	
	/*安全*/
	private static boolean bool = false;
	/*不安全*/
	private static Boolean Bool = false;
	
	public static void main(String[] args) throws InterruptedException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		final Semaphore semaphore = new Semaphore(threadTotal); //计数信号量
		final CountDownLatch countDownLatch = new CountDownLatch(clientTotal); //计数器闭锁
			for (int i = 0; i < clientTotal; i++) {
				executorService.execute(() -> {
					try {
						semaphore.acquire(); //获取一个许可证（阻塞一个线程）
						test();
						semaphore.release(); //释放一个许可证（释放一个线程）
					} catch (Exception e) {
						log.error("exception:{}",e);
					}
					countDownLatch.countDown();   //计数减1
				});
				
			}
		countDownLatch.await();  //当计数为0时唤醒
		log.info("atomicBoolean最终结果：{}",atomicBoolean.get());
		System.exit(0);
	}
	public static void test() { 
		if(!Bool) {
			Bool = true;
			log.info("Bool更新");
		}
		if(!bool) {
			bool = true;
			log.info("bool更新");
		}
		
		if(atomicBoolean.compareAndSet(false, true)) {
			log.info("atomicBoolean更新：{}",atomicBoolean.get());
		}
	}
	
}
