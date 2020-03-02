package com.polaris.technicalintegration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 对Redis数据库进行操作的工具类
 * @author jicexosl
 */
@Component
public class JedisUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JedisUtil.class);

	private static final int ACQUIRE_TIMEOUT = 10;
	private static final int LOCK_TIMEOUT = 30;
	private static final String LOCK_KEY_PREFIX = "lock:";
	
	static String redisHost;
	static int redisPort;
	static String redisPassword;
	static int redisDatabase;
	static int timeout;

	@Value("${spring.redis.host}")
	public void setRedisHost(String redisHost) {
		JedisUtil.redisHost = redisHost;
	}
	@Value("${spring.redis.port}")
	public void setRedisPort(int redisPort) {
		JedisUtil.redisPort = redisPort;
	}
	@Value("${spring.redis.password}")
	public void setRedisPassword(String redisPassword) {
		JedisUtil.redisPassword = redisPassword;
	}
	@Value("${spring.redis.database}")
	public void setRedisDatabase(int redisDatabase) {
		JedisUtil.redisDatabase = redisDatabase;
	}
	@Value("${spring.redis.timeout}")
	public void setTimeout(int timeout) {
		JedisUtil.timeout = timeout;
	}

	private JedisUtil() {
	}


	// 单例，延迟加载
	private static class SingletonHolder {
		private static JedisPool pool;
		static {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(1024);
			config.setMaxIdle(20);
			pool = new JedisPool(config, redisHost,
					redisPort, timeout,
					redisPassword, redisDatabase);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					pool.destroy();
				}
			});
		}
	}

	// 获取连接池
	private static JedisPool getPool() {
		return SingletonHolder.pool;
	}

	// 从连接池中取出一个连接
	public static Jedis getJedis() {

		//原来Jedis的方案
		return getPool().getResource();

	}

	// 关闭连接
	private static void closeJedis(Jedis jedis) {
		if (null != jedis) {
			jedis.close();
		}
	}

	/**
	 * 判断一个key存不存在
	 * @param key
	 * @return
	 */
	public static Boolean exists(String key){
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.exists(key);
		}catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return false;
	}

	// 保存字符串值
	public static boolean saveString(String key, String value) {
		return saveStringValue(key, value, null);
	}

	// 保存字符串值并设置有效时间
	public static boolean saveStringAndSetExpire(String key, String value, int valid) {
		return saveStringValue(key, value, valid);
	}

	private static boolean saveStringValue(String key, String value, Integer valid) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.set(key, value);
			if (valid != null) {
				jedis.expire(key, valid);
			}
		} catch (Exception e) {
			LOG.error("redis error , the key is " + key + " and the value is " + value, e);
			return false;
		} finally {
			closeJedis(jedis);
		}
		return true;
	}

	// 根据key取出值
	public static String getString(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.get(key);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	// 删除指定的键值
	public static void removeByKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.del(key);
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
	}

	// 保存值到hashMap
	public static boolean saveField(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hset(key, field, value);
		} catch (Exception e) {
			LOG.error("redis error ", e);
			return false;
		} finally {
			closeJedis(jedis);
		}
		return true;
	}

	public static boolean setExpire(String key, int valid) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.expire(key, valid);
			return true;
		} catch (Exception e) {
			LOG.error("redis error ", e);
			e.printStackTrace();
			return false;
		} finally {
			closeJedis(jedis);
		}
	}

	// 取出hashMap中对应字段的值
	public static String getFieldValue(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hget(key, field);
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	// 取出hashMap
	public static Map<String, String> getMap(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	public static boolean saveMap(String key, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hmset(key, map);
			return true;
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
		return false;
	}

	// 将对象序列化(json格式化)存入redis
	public static boolean saveObj(String key, Object object) {
		try {
			String value = JsonUtils.toJsonString(object);
			return saveString(key, value);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 将对象序列化后存入map
	public static boolean saveObj(String key, String field, Object object) {
		try {
			String value = JsonUtils.toJsonString(object);
			return saveField(key, field, value);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 从redis的map中取出对象
	public static <T> T getObj(String key, String field, Class<T> clazz) throws IOException {
		String value = getFieldValue(key, field);
		if (value == null) {
			return null;
		}
		return JsonUtils.jsonToObject(value, clazz);
	}

	// 从redis中取出对象
	public static <T> T getObj(String key, Class<T> clazz) throws IOException {
		String value = getString(key);
		if (value == null) {
			return null;
		}
		return JsonUtils.jsonToObject(value, clazz);
	}

	// 将一个列表保存至redis
	public static <T> boolean saveList(String key, List<T> list) {
		if (list == null) {
			return false;
		}
		int size = list.size();
		String[] array = new String[size];
		Jedis jedis = null;
		try {
			//redis存储list是倒序的
			for (int i = size - 1, j = 0; i >= 0; i--, j++) {
				array[j] = JsonUtils.toJsonString(list.get(i));
			}
			jedis = getJedis();
			jedis.del(key);
			jedis.lpush(key, array);
			return true;
		} catch (Exception e) {
			LOG.info("Exception:", e);
			return false;
		} finally {
			closeJedis(jedis);
		}
	}

	// 从redis中取出列表
	public static <T> List<T> getList(String key, Class<T> clazz) {
		Jedis jedis = null;
		List<T> result = new ArrayList<T>();
		try {
			jedis = getJedis();
			List<String> list = jedis.lrange(key, 0, -1);
			for (String string : list) {
				T t = JsonUtils.jsonToObject(string, clazz);
				result.add(t);
			}
		} catch (IOException e) {
			LOG.error("json to object error", e);
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	/**
	 * 得到值
	 * @param key
	 * @param refreshSource
	 * @param expiry 有效期
	 * @return
	 */
	public static String get(String key, Callable<String> refreshSource, int expiry) {
		String value = get(key);
		if (null == value) {
			try {
				value = refreshSource.call();
				if (null != value && value != "") {
					set(key, value, expiry);
				}
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		return value;
	}

	/**
	 * 得到值
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.get(key);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	public static long lrem(String list, long count, String value) {
		Jedis jedis = null;
		long result = 0L;
		try {
			jedis = getJedis();
			result = jedis.lrem(list, count, value);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + list, e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	/**
	 * 设值
	 * @param key
	 * @param value
	 * @param expire
	 */
	public static void set(String key, String value, int expire) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.set(key, value);
			if (expire > 0) {
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			LOG.error("redis error , the key is " + key + " and the value is " + value, e);
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 在key对应list的头部添加字符串元素
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long lpush(String key, String... values) {
		Jedis jedis = null;
		Long value = null;
		try {
			jedis = getJedis();
			value = jedis.lpush(key, values);
		} catch (Exception e) {
			LOG.error("redis list error ");
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	/**
	 * 从list的头部删除元素，并返回删除元素
	 * @param key
	 * @return
	 */
	public static String lpop(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.lpop(key);
		} catch (Exception e) {
			LOG.error("redis list error ");
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	/**
	 * 在key对应list的尾部添加字符串元素
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long rpush(String key, String... values) {
		Jedis jedis = null;
		Long value = null;
		try {
			jedis = getJedis();
			value = jedis.rpush(key, values);
		} catch (Exception e) {
			LOG.error("redis list error ");
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	/**
	 * 从list的尾部删除元素，并返回删除元素
	 * @param key
	 * @return
	 */
	public static String rpop(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getJedis();
			value = jedis.rpop(key);
		} catch (Exception e) {
			LOG.error("redis list error ");
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	public static Set<String> keys(String pattern) {
		Jedis jedis = null;
		Set<String> value = null;
		try {
			jedis = getJedis();
			value = jedis.keys(pattern);
		} catch (Exception e) {
			LOG.error("redis keys error ");
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	public static void delKeyByPattern(String key) {
		Set<String> set = keys(key);
		for (String str : set) {
			removeByKey(str);
		}
	}

	//获取过期时间
	public static long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.ttl(key);
		} catch (Exception e) {
			LOG.error("redis get expired time error , the key is " + key);
			return -1;
		} finally {
			closeJedis(jedis);
		}
	}

	public static String acquireLock(String lockName, int acquireTimeout, int lockTimeout) {
		Jedis jedis = null;

		try {
			jedis = getJedis();

			String identifier = UUID.randomUUID().toString();
			long end = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < end) {
				String lockKey = LOCK_KEY_PREFIX + lockName;
				Long flag = jedis.setnx(lockKey, identifier);
				if (flag == 1) {
					jedis.expire(lockKey, lockTimeout);
					return identifier;
				} else if (jedis.ttl(lockKey) == -1) {
					jedis.expire(lockKey, lockTimeout);
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					LOG.error("acquireLock() InterruptedException", e);
				}
			}
		} catch (Exception e) {
			LOG.error(String.format("acquire lock exception: [%s][%s]", lockName, e.getMessage()), e);
		} finally {
			closeJedis(jedis);
		}

		return null;
	}

	public static String acquireLock(String lockName) {
		return acquireLock(lockName, ACQUIRE_TIMEOUT, LOCK_TIMEOUT);
	}

	public static void releaseLock(String lockName, String identifier) {
		Jedis jedis = null;

		try {
			jedis = getJedis();
			String lockKey = LOCK_KEY_PREFIX + lockName;
			if (identifier.equals(jedis.get(lockKey))) {
				jedis.del(lockKey);
			}
		} catch (Exception e) {
			LOG.error(String.format("release lock exception: [%s][%s][%s]", lockName, identifier, e.getMessage()), e);
		} finally {
			closeJedis(jedis);
		}

	}

	public static long setNx(String redisKey, String content, int expireTime) {
		Jedis jedis = null;

		try {
			if (content != null) {
				jedis = getJedis();
				Long flag = jedis.setnx(redisKey, content);
				if (flag == 1) {
					jedis.expire(redisKey, expireTime);
				}
				return flag;
			}
		} catch (Exception e) {
			LOG.error("setNx exception", e);
			return 0L;
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	public static long setNxWithoutTTL(String redisKey, String content) {
		Jedis jedis = null;
		try {
			if (content != null) {
				jedis = getJedis();
				Long flag = jedis.setnx(redisKey, content);
				return flag;
			}
		} catch (Exception e) {
			LOG.error("setNx exception", e);
			return 0L;
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	public static boolean setNxResult(String redisKey, String content, int expireTime) {
		Jedis jedis = null;

		try {
			if (content != null) {
				jedis = getJedis();
				Long flag = jedis.setnx(redisKey, content);
				if (flag == 1) {
					jedis.expire(redisKey, expireTime);
					return true;
				}
			}
		} catch (Exception e) {
			LOG.error("setNx exception", e);
			return false;
		} finally {
			closeJedis(jedis);
		}
		return false;
	}

	public static long hincrby(String key, String field,Integer increment) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hincrBy(key, field, increment);
		} catch (Exception e) {
			LOG.error("redis error ", e);
		} finally {
			closeJedis(jedis);
		}
		return 0;
	}

	public static Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	public static void hdel(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hdel(key,field);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
	}

	public static boolean hexists(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hexists(key,field);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
			return false;
		} finally {
			closeJedis(jedis);
		}
	}

	public static Long hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
			return -1L;
		} finally {
			closeJedis(jedis);
		}
	}

	public static Set<String> hkeys(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hkeys(key);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	public static Long incrby(String key, long i) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.incrBy(key, i);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	public static void decrttl(String key, long i) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			long ttl = jedis.ttl(key);
			jedis.decrBy(key, i);
			jedis.expire(key, (int)ttl);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
	}

	public static Boolean sismember(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.sismember(key, value);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return false;
	}

	public static Long sadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	public static Long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.llen(key);
		} catch (Exception e) {
			LOG.error("redis error,the key is " + key, e);
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}
}
