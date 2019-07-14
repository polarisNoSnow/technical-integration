package com.polaris.technicalintegration.singleton;

import com.polaris.technicalintegration.annotation.UnThreadSafe;

/**
 * 单例模式:懒汉模式
 * @author polaris
 * @date 2018年9月22日
 */
@UnThreadSafe
public class Singleton1 {
	private Singleton1() {}
	
	private static Singleton1 instance = null;
	
	public static Singleton1 getInstance() {
		if(instance == null) {   //此处线程不安全
			return new Singleton1();
		}
		return instance;
	}
}
