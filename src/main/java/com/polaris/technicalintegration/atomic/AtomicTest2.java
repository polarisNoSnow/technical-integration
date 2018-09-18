package com.polaris.technicalintegration.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import com.polaris.technicalintegration.annotation.ThreadSafe;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author polaris
 * @date 2018年9月5日
 */
@Slf4j
@ThreadSafe
public class AtomicTest2 {
	/*模拟客户端数*/
	private static int clientTotal = 5000;
	/*并发线程数据*/
	private static int threadTotal = 200;
	
	/*计数器*/
	/**
	 * 通过底层CAS的方式
	 * 1.获取底层值
	 * 2.用底层的值与当前的值比较，相同则继续相加操作，否则继续获取底层值（do-while循环）
	 */
	private static AtomicInteger count1 = new AtomicInteger(0); 
	/**
	 * AtomicInteger在高并发的时候，两值不等的几率大大增加，会造成更多的循环次数，影响性能
	 * LongAdder将之前单个节点的并发分散到各个节点的，这样从而提高在高并发时候的效率
	 * 缺点：LongAdder在统计的时候如果有并发更新，可能导致统计的数据有误差
	 */
	private static LongAdder count2 = new LongAdder();
	
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
		log.info("AtomicInteger当前计数：{}",count1);
		log.info("LongAdder当前计数：{}",count2);
		System.exit(0);
	}
	
	public static void add() {
		count1.incrementAndGet();
		count2.increment();
	}
}
