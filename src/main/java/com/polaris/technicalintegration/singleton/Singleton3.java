package com.polaris.technicalintegration.singleton;

import com.polaris.technicalintegration.annotation.UnThreadSafe;

/**
 * 单例模式:双重同步锁模式
 * 
 * 理想执行步骤：
 * 1.memery = allocate() 分配对象的内存空间
 * 2.ctorInstance 初始化对象
 * 3.instance = memery 将指针指向刚分配的内存空间
 * 实际会出现指令重排情况如1、3、2，并发返回的instance是未被初始化的
 * 
 * @author polaris
 * @date 2018年9月22日
 */
@UnThreadSafe
public class Singleton3 {
	private Singleton3() {}
	
	private static Singleton3 instance = null;
	
	public static Singleton3 getInstance() {
		if(instance == null) {				//双重检测
			synchronized(instance) {		//枷锁机制
				if(instance == null) {
					return new Singleton3();   //虽然概率很小，但-此处存在指令重排的问题
				}
			}
		}
		return instance;
	}
}
