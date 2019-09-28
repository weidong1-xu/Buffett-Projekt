package com.hanslv.stock.selector.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.jboss.logging.Logger;

import com.hanslv.stock.selector.commons.constants.MyBatisConfigConstants;

/**
 * MyBatis操作工具类，单例
 * 其他模块使用时需要在当前项目的src/main/resource目录下创建mybatis/config目录结构，并将MyBatis的配置文件放置在该目录下以mybatis-config.xml命名并设置UTF-8编码
 * junit环境下可以在src/test/resource以同样的方式创建目录和文件
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
		try(InputStream inputStream = MyBatisUtil.class.getResourceAsStream(MyBatisConfigConstants.CONFIG_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStreamReader);
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
