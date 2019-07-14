package com.polaris.technicalintegration.publish;

import java.util.Arrays;

import com.polaris.technicalintegration.annotation.UnThreadSafe;

import lombok.extern.slf4j.Slf4j;
/**
 * 这里的不安全在于对象中的属性可以被任意修改
 * @author polaris
 * @date 2018年9月21日
 */
@Slf4j
@UnThreadSafe
public class UnsafePublish {
	public int[] array = {0,1,2};
	
	public int[] getArray() {
		return array;
	}
	
	public static void main(String[] args) {
		UnsafePublish publish = new UnsafePublish();
		log.info(Arrays.toString(publish.array));
		publish.array[0] = 3;
		log.info(Arrays.toString(publish.array));
	}
}
