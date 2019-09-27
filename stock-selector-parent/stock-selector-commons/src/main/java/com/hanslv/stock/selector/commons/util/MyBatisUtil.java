package com.hanslv.stock.selector.commons.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.jboss.logging.Logger;

import com.hanslv.stock.selector.commons.constants.MyBatisConfigConstants;

/**
 * MyBatis操作工具类单例
 * 
 * ------------------------------------------
 * 1、获取单例MyBatis实例											public static MyBatisUtil getInstance()	
 * 2、获取SqlSession													public SqlSession getConnection()
 * 3、关闭SqlSession													public SqlSession getConnection()
 * 4、提交Session													public void commitConnection()
 * ------------------------------------------
 * @author harrylu
 *
 */
public class MyBatisUtil {
	private static class Singleton{
		private static final MyBatisUtil INSTANCE = new MyBatisUtil();
	}
	
	Logger logger = Logger.getLogger(MyBatisUtil.class);	
	private ThreadLocal<SqlSession> sessionThreadLocal;
	private SqlSessionFactory sqlSessionFactory;
	
	private MyBatisUtil() {
		/**
		 * 实例化ThreadLocal
		 */
		sessionThreadLocal = new ThreadLocal<>();
		
		/**
		 * 初始化SqlSessionFactory
		 */
		SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
		try(FileInputStream fileInputStream = new FileInputStream(MyBatisConfigConstants.CONFIG_PATH)){
			sqlSessionFactory = sqlSessionFactoryBuilder.build(fileInputStream);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 1、获取单例MyBatis实例
	 * @return
	 */
	public static MyBatisUtil getInstance() {
		return Singleton.INSTANCE;
	}
	
	
	
	/**
	 * 2、获取SqlSession
	 * @return
	 */
	public SqlSession getConnection() {
		SqlSession session = sessionThreadLocal.get();
		if(session == null) {
			session = sqlSessionFactory.openSession();
			sessionThreadLocal.set(session);
			logger.info(Thread.currentThread() + " 创建了一个SqlSession！");
		}
		return session;
	}
	
	
	/**
	 * 3、关闭SqlSession
	 */
	public void closeConnection() {
		SqlSession session = sessionThreadLocal.get();
		if(session != null) {
			session.close();
			logger.info(Thread.currentThread() + " 关闭了一个SqlSession！");
		}
	}
	
	
	/**
	 * 4、提交Session
	 */
	public void commitConnection() {
		SqlSession session = sessionThreadLocal.get();
		if(session != null) {
			session.commit();
			logger.info(Thread.currentThread() + " 提交了数据！");
		}
	}
}
