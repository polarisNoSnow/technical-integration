package com.polaris.technicalintegration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 非线程安全类注释
 * @author polaris
 *
 */
@Target(ElementType.TYPE) //Annotation所修饰的对象范围
@Retention(RetentionPolicy.SOURCE) //Annotation被保留的范围
public @interface UnThreadSafe {
	String value() default "";
}
