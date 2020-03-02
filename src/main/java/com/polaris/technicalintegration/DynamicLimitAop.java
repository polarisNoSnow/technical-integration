package com.polaris.technicalintegration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.polaris.technicalintegration.annotation.RateLimiter;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @Description 限流器<br/>
 * 1.判断请求是否被限流；<br/>
 * 2.如果被限流就将参数发到kafka，kafka接收后重新开始（所以可能多次被限制）；<br/>
 * 3.未被限流就记录下调用次数（可以用于后期统计），继续业务处理。
 * @author polaris
 * @date 2020年2月26日
 */
@Slf4j
@Aspect
@Component
public class DynamicLimitAop {

    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private StringRedisTemplate redisStringTemplate;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaTemplate kafkaTemplate;
    
    private String emailCacheKey = "polaris:rateLimiter:email:";
    private long EMAIL_TIME_OUT = 60;

    @Around("@annotation(rateLimiter)")
    public Object execute(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().getName();
        int limit = rateLimiter.limit();
        int timeout = rateLimiter.timeout();
        String name = "polaris:ratelimit:" + methodName + ":";
        Jedis jedis = JedisUtil.getJedis();
        Boolean success = null;
        long ttl = 0L;
        String currentCount = null;
        try {
            success = CountRateLimiter.acquire(jedis, limit, timeout, name);
            ttl = (ttl=jedis.ttl(name)) > 0 ? ttl : 0;
            currentCount = jedis.get(name);
        } catch (Exception e) {
            log.error("DynamicLimitAop redis分布式限流异常：{}", e);
        } finally {
            jedis.close();
        }

        try {
        	/*
        	 * 如果被限频，将相关参数记录下来等待之后的继续运行
        	 */
            if (!success) {
                log.warn("动态拉取数据接口超频：当前频次：{}，限制频次：{}，休眠：{}秒", currentCount, limit, ttl);
                //kafkaTemplate.send(methodName+"_error", getRequestParamsJson(joinPoint));
            } else {
                recordDerbyCallNum();
                return joinPoint.proceed();
            }
        } catch (Throwable e) {
            log.error("DynamicLimitAop异常：{}", e);
            dealError(success, ttl, methodName, rateLimiter, e);
        }
        return "被限流";
    }


    /**
     * 参数转为json
     *
     * @param joinPoint
     * @return
     */
    private String getRequestParamsJson(ProceedingJoinPoint joinPoint) {
        RequestParam requestParam = RequestParam.builder()
                .request((String) joinPoint.getArgs()[0])
                .data((String) joinPoint.getArgs()[1])
                .build();
        return JSONObject.toJSONString(requestParam);
    }


    /**
     * 处理异常
     *
     * @param success
     * @param ttl
     * @param methodName
     * @param rateLimiter
     * @param e
     */
    private void dealError(Boolean success, long ttl, String methodName, RateLimiter rateLimiter, Throwable e) {
        String remark = methodName;
        if (StringUtils.isNotBlank(rateLimiter.bizRemark())) {
            remark = rateLimiter.bizRemark();
        }
        String content = remark + "指定次数[" + rateLimiter.limit() + "]次，" +
                "总限定次数为[" + rateLimiter.totalNum() + "]次, 剩余时间[" + ttl + "]秒，是否超频：" + success +
                " DynamicLimitAop异常，异常信息：" + e.getMessage();
        String subject = "服务-" + rateLimiter.bizRemark() + "告警";
        
        /**
         * 	防止邮件多发
         */
        String key = emailCacheKey + subject;
        //如果key永久存在则重新设定时间(redis版本不同ttl返回值不同，所以需要再判断key是否存在)
        if(redisStringTemplate.getExpire(key, TimeUnit.SECONDS) < 0 && redisStringTemplate.hasKey(key) ) {
        	redisStringTemplate.expire(key,EMAIL_TIME_OUT,TimeUnit.SECONDS); 
        }
        Boolean emailCacheValue =  redisStringTemplate.opsForValue().setIfAbsent(key, "");
        //如果设置成功则需要发送邮件（如果redis宕机则key永久存在，解决办法如上）
        if(emailCacheValue) {
        	redisStringTemplate.expire(key,EMAIL_TIME_OUT,TimeUnit.SECONDS); 
        	emailService.sendEmail(subject, content);
        }else {
        	log.info(subject+":"+content);
        }
        
    }


    /**
     * 记录调用次数
     */
    private void recordDerbyCallNum() {
        String prefixKey = "polaris:rateLimiter:num:";
        String hourNumKey = prefixKey + dateToFormatStr(new Date(), this.YYYYMMDDHH);
        String dayNumKey = prefixKey + dateToFormatStr(new Date(), this.YYYYMMDD);
        String monthNumKey = prefixKey + dateToFormatStr(new Date(), this.YYYYMM);
        long result = redisTemplate.opsForValue().increment(hourNumKey, 1L);
        if (result < 2) {
            //针对小时的记录保存8天
            redisTemplate.expire(hourNumKey, 8 * 60 * 60 * 24, TimeUnit.SECONDS);
        }
        redisTemplate.opsForValue().increment(dayNumKey, 1L);
        redisTemplate.opsForValue().increment(monthNumKey, 1L);
    }
    
    /**
     * 将日期转成字符串
     *
     * @param date      日期
     * @param formatStr 格式
     * @return 日期字符串
     */
    public static String dateToFormatStr(Date date, String formatStr) {
        if (date == null) {
            return "Date is null.";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }
    
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    
    public static void main(String[] args) {
    	long ttl = 0l;
    	 ttl = 5l > 0 ? ttl : 0;
		System.out.println(ttl);
	}
}
