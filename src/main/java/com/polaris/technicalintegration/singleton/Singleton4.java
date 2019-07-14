package com.polaris.technicalintegration.singleton;

import com.polaris.technicalintegration.annotation.ThreadSafe;

/**
 * 单例模式:双重同步锁模式
 * volatile禁止指令重排
 * 
 * @author polaris
 * @date 2018年9月22日
 */
@ThreadSafe
public class Singleton4 {
	private Singleton4() {}
	
	private static volatile Singleton4 instance = null;
	
	public static Singleton4 getInstance() {
		if(instance == null) {				//双重检测
			synchronized(Singleton4.class) {		//枷锁机制
				if(instance == null) {
					return new Singleton4();   
				}
			}
		}
		return instance;
	}
}
