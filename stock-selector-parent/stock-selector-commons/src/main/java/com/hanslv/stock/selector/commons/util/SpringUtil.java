package com.hanslv.stock.selector.commons.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类，在容器外部调用容器内部的Bean
 * 
 * -------------------------------------------------
 * 1、根据类型获取一个Bean												public <T> T getBeanFromSpringBoot(Class<T> clazz)
 * 2、根据类型和名称获取一个Bean											public static <T> T getBean(String name, Class<T> clazz) throws ClassNotFoundException
 * -------------------------------------------------
 * @author admin
 *
 */
@Component
public class SpringUtil implements ApplicationContextAware{
	private static ApplicationContext applicationContext;
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(SpringUtil.applicationContext == null)
			SpringUtil.applicationContext = applicationContext;
	}

	/**
	 * 1、根据类型获取一个Bean
	 * @param clazz
	 * @return
	 */
	public static <T> T getBeanFromSpringBoot(Class<T> clazz) {
		return SpringUtil.applicationContext.getBean(clazz);
	}
	
	
	/**
	 * 2、根据类型和名称获取一个Bean
	 * @param name
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
    public static <T> T getBeanFromSpringBoot(String name, Class<T> clazz) throws ClassNotFoundException {
        return SpringUtil.applicationContext.getBean(name, clazz);
    }
}
