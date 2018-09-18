package com.polaris.technicalintegration.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.polaris.technicalintegration.annotation.UnThreadSafe;

import lombok.extern.slf4j.Slf4j;

/**
 * volatile 从主存获取数据、并且刷新到主存,（多线程下就会产生覆盖刷新，不具备原子性），
 * 一般使用在状态标记中，如：根据线程一初始化完成后 修改状态值，线程二获取状态后开始初始化
 * @author polaris
 * @date 2018年9月5日
 */
@Slf4j
@UnThreadSafe
public class AtomicTest5 {
	/*模拟客户端数*/
	private static int clientTotal = 20000;
	/*并发线程数据*/
	private static int threadTotal = 200;
	/*计数器*/
	private static volatile int count = 0;
	
	public static void main(String[] args) throws InterruptedException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		final Semaphore semaphore = new Semaphore(threadTotal); //计数信号量
		final CountDownLatch countDownLatch = new CountDownLatch(clientTotal); //计数器闭锁
			for (int i = 0; i < clientTotal; i++) {
				executorService.execute(() -> {
					try {
						semaphore.acquire(); //获取一个许可证（阻塞一个线程）
						add();
						semaphore.release(); //释放一个许可证（释放一个线程）
					} catch (Exception e) {
						log.error("exception:{}",e);
					}
					countDownLatch.countDown();   //计数减1
				});
				
			}
		countDownLatch.await();  //当计数为0时唤醒
		log.info("int当前计数：{}",count);
		System.exit(0);
	}
	
	public static void add() {
		count++;
	}
}
