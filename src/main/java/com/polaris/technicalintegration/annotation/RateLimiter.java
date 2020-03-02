package com.polaris.technicalintegration.annotation;

import java.lang.annotation.*;


/**
 * @Description: 开启分布式限流
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
	int limit() default 100; //限定次数
    int timeout() default 1; //超时时间
    int totalNum() default 0;
    int warnType() default 1;  //告警类型:1.超时时间内发送一次；其他.超出限定次数，每次都发送
    boolean warnFlag() default true;  //是否告警
    String bizAppId() default  "travel-hotel-data-2b";  //告警对接id
    int bizType() default  7;  //告警对接类型
    int bizStatus() default  2;  //告警对接状态
    String bizRemark() default "";
}
