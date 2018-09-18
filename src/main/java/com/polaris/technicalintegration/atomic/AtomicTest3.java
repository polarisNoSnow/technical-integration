package com.polaris.technicalintegration.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

import com.polaris.technicalintegration.annotation.ThreadSafe;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * AtomicReference相关，重要性低
 * AtomicStampedReference采用版本号更新，解决CAS的ABA问题
 * @author polaris
 * @date 2018年9月5日
 */
@Slf4j
@ThreadSafe
public class AtomicTest3 {
	
	@Setter
	private volatile int c = 100;
	
	/*计数器*/
	private static AtomicReference<Integer> count = new AtomicReference<Integer>(0); 
	
	private static AtomicIntegerFieldUpdater<AtomicTest3> updator = AtomicIntegerFieldUpdater.newUpdater(AtomicTest3.class, "c");
	
	public static void main(String[] args) throws InterruptedException {
		
		/*AtomicReference测试*/
		count.compareAndSet(0, 1); //如果是0则更新为1
		count.compareAndSet(0, 2);
		count.compareAndSet(1, 111);
		count.compareAndSet(2, 222);
		log.info("count:{}",count.get());
		
		/*AtomicIntegerFieldUpdater测试*/
		AtomicTest3 atomicTest3 = new AtomicTest3();
		updator.compareAndSet(atomicTest3, 100, 120);
		updator.compareAndSet(atomicTest3, 100, 200);
		log.info("updator:{}",atomicTest3.c);
		
	}
	
}
