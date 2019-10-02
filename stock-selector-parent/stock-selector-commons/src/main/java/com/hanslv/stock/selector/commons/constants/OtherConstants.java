package com.hanslv.stock.selector.commons.constants;
/**
 * 公用常量
 * 
 * -----------------------------------------------
 * 1、项目根目录									CLASS_PATH
 * -----------------------------------------------
 * @author harrylu
 *
 */
public abstract class OtherConstants {
	public static final String CLASS_PATH = OtherConstants.class.getClassLoader().getResource("").toString().replaceAll("file:/", "");//项目根目录
}
