package com.polaris.technicalintegration.singleton;

import com.polaris.technicalintegration.annotation.ThreadSafe;

/**
 * 枚举模式：推荐
 * 
 * @author polaris
 * @date 2018年9月22日
 */
@ThreadSafe 
public class Singleton5 {
	public Singleton5() {

	}
	
	public Singleton5 getSingleton5() {
		return Singleton.SINGLETON5.getInstance();
	}
	
	private enum Singleton{
		SINGLETON5;
		
		private Singleton5 singleton;
		
		//jvm保证只调用一次
		Singleton() {
			singleton = new Singleton5();
		}
		
		Singleton5 getInstance(){
			return singleton;
		}
	}
}
