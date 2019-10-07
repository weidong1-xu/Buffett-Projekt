package com.hanslv.stock.selector.commons.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{
	
	/**
	 * priceInfoRedisTemplate
	 * @param connectionFactory
	 * @return
	 */
	@Bean("priceInfoRedisTemplate")
	public RedisTemplate<Object , Object> priceInfoRedisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<Object , Object> redisTemplate = new RedisTemplate<>();
		
		/*
		 * 配置连接工厂
		 */
		redisTemplate.setConnectionFactory(connectionFactory);
		
		/*
		 * 指定序列化器
		 */
		Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL , JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jacksonSeial.setObjectMapper(objectMapper);

		redisTemplate.setValueSerializer(jacksonSeial);
		redisTemplate.setKeySerializer(jacksonSeial);
		
		return redisTemplate;
	}
	
	
	/**
	 * basicRedisTemplate
	 * @param connectionFactory
	 * @return
	 */
	@Bean("basicRedisTemplate")
	public RedisTemplate<Object , Object> basicRedisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<Object , Object> redisTemplate = new RedisTemplate<>();
		/*
		 * 配置连接工厂
		 */
		redisTemplate.setConnectionFactory(connectionFactory);
		
		return redisTemplate;
	}
}
