package com.polaris.technicalintegration.singleton;

import com.polaris.technicalintegration.annotation.ThreadSafe;

/**
 * 单例模式：饿汉模式
 * @author polaris
 * @date 2018年9月22日
 */
@ThreadSafe
public class Singleton2 {
	private Singleton2() {}
	
	private static Singleton2 instance = new Singleton2();
	
	public static Singleton2 getInstance() {
		return instance;
	}
}
