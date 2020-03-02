package com.polaris.technicalintegration;

import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * @Description: Redis实现分布式限流（计数算法+lua）
 */
public class CountRateLimiter {
	
	/**
     * @param jedis 
     * @param limit 阈值/单位时间
     * @param seconds 单位时间 秒
     * @param name 名称
     * @return
     * @throws IOException
     * @Description 处理流程：<br/>
     * 1.初始化各参数；<br/>
     * 2.计算被限流的方法（key）的次数current；<br/>
     * 3.如果current小于等于1则设置key过期时间（expire）；<br/>
     * 4.如果current大于阈值limit，返回false，反之返回true。
     */
    public static boolean acquire(
            Jedis jedis, int limit, int seconds,String name) throws IOException {
    	//Incrby：将key中储存的数字加上指定的增量值。如果key不存在，那么key的值会先被初始化为0，然后再执行INCRBY命令。
        //expire：用于设置 key 的过期时间，key 过期后将不再可用。单位以秒计。
        String luaScript =    "local key = KEYS[1] "
                            + "local limit = tonumber(ARGV[1]) "
                            + "local timeout = tostring(ARGV[2]) "
                            + "local current = tonumber(redis.call(\"INCRBY\", key,\"1\")) "
                            + "if current <= 1 then "
                            + "   redis.call(\"expire\", key,timeout) "
                            + "end "
                            + "if current  > limit then "
                            + "   return 0 "
                            + "else "
                            + "   return 1 "
                            + "end";
        return (Long)jedis.eval(luaScript ,Lists.newArrayList(name), Lists.newArrayList(String.valueOf(limit),String.valueOf(seconds))) == 1;
    }
}
