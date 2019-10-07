package com.hanslv.stock.selector.commons.constants;
/**
 * 公用常量
 * 
 * -----------------------------------------------
 * 1、项目根目录									CLASS_PATH
 * 2、基本线程池大小								BASIC_THREAD_POOL_SIZE
 * 3、基本阻塞队列大小							BASIC_BLOCKING_QUEUE_SIZE
 * -----------------------------------------------
 * @author harrylu
 *
 */
public abstract class CommonsOtherConstants {
	public static final String CLASS_PATH = CommonsOtherConstants.class.getClassLoader().getResource("").toString().replaceAll("file:/", "");//项目根目录
	public static final int BASIC_THREAD_POOL_SIZE = 20;//基本线程池大小
	public static final int BASIC_BLOCKING_QUEUE_SIZE = 500;//基本阻塞队列大小
}
