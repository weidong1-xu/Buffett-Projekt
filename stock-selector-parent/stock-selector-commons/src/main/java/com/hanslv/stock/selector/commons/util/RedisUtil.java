package com.hanslv.stock.selector.commons.util;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis工具类
 * 
 * ----------------------------------------
 * 1、设置Key的失效时间												public boolean setExpire(Object key , long expireTime , TimeUnit timeUnit)
 * 2、获取指定Key的失效时间											public long getExpire(Object key , TimeUnit timeUnit)
 * 3、删除多个或单个key												public boolean deleteKeys(Object ... keys)
 * 4、放入一个key value												public boolean setKey(Object key , Object value)
 * 5、获取一个key对应的value											public Object get(Object key)
 * 6、设置key value同时设置超时时间									public boolean setWithExpire(Object key , Object value , long expireTime , TimeUnit timeUnit)
 * ----------------------------------------
 * @author hanslv
 *
 */
@Component
public class RedisUtil {
	
	/*
	 * Redis操作对象
	 */
	@Autowired
	@Qualifier("priceInfoRedisTemplate")
	private RedisTemplate<Object , Object> redisTemplate;
	
	
	/**
	 * 1、设置Key的失效时间
	 * @param key 被设置的key值
	 * @param expireTime 设置的失效时间
	 * @param timeUnit 失效时间单位
	 * @return
	 */
	public boolean setExpire(Object key , long expireTime , TimeUnit timeUnit) {
		if(expireTime > 0) {
			redisTemplate.expire(key, expireTime, timeUnit);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * 2、获取指定Key的失效时间
	 * @param key
	 * @param timeUnit
	 * @return
	 */
	public long getExpire(Object key , TimeUnit timeUnit) {
		return redisTemplate.getExpire(key , timeUnit);
	}
	
	
	/**
	 * 3、删除多个或单个key
	 * @param keys
	 * @return
	 */
	public boolean deleteKeys(Object ... keys) {
		if(keys != null && keys.length > 0) {
			if(keys.length == 1) 
				redisTemplate.delete(keys[0]);
			else
				redisTemplate.delete(Stream.of(keys).collect(Collectors.toList()));
			return true;
		}else
			return false;
	}
	
	
	/**
	 * 4、放入一个key value
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setKey(Object key , Object value) {
		redisTemplate.opsForValue().set(key , value);
		return true;
	}
	
	
	/**
	 * 5、获取一个key对应的value
	 * @param key
	 * @return
	 */
	public Object get(Object key) {
		if(key != null) {
			Object result = redisTemplate.opsForValue().get(key);
			return result == null ? null : result;
		}else
			return null;
	}
	
	
	/**
	 * 6、设置key value同时设置超时时间
	 * @param key
	 * @param value
	 * @param expireTime
	 * @param timeUnit
	 * @return
	 */
	public boolean setWithExpire(Object key , Object value , long expireTime , TimeUnit timeUnit) {
		if(key != null && value != null && expireTime != 0) {
			redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
			return true;
		}else
			return false;
	}
	
}
