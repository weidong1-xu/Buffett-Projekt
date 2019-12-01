package com.hanslv.crawler.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jboss.logging.Logger;


/**
 * 爬虫常量类
 * ------------------------------------------
 * 
 * ------------------------------------------
 * @author harrylu
 *
 */
public abstract class CrawlerConstants {
	static Logger logger = Logger.getLogger(CrawlerConstants.class);
	private static final String PROP_PATH = "/crawler.properties";//配置文件URL
	
	public static String stockInfoTargetUrl;//股票基本信息爬取目标地址
	
	public static String stockPriceTargetUrlPrefix;//股票价格信息爬取目标地址前缀
	public static String stockPriceTargetShangzhengUrlSuffix;//上证股票价格信息爬取目标地址后缀
	public static String stockPriceTargetShenzhengUrlSuffix;//深证股票价格信息爬取目标地址后缀
	
	public static String stockEncoding;//股票返回信息编码格式
	
	public static  String shangzhengStockInfoCssSelector;//上证股票信息CSS选择器
	public static  String shenzhengStockInfoCssSelector;//深证股票信息CSS选择器
	
	
	public static final String PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX = "tab_stock_price_shangzheng_";//价格信息分表上证表名称前缀
	public static final String PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX = "tab_stock_price_shenzheng_";//价格信息分表深证表名称前缀
	public static final int STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE = 10;//插入股票价格信息的线程池大小
	public static final int PRICE_INFO_MESSAGE_QUEUE_SIZE = 10;
	
	static {
		/**
		 * 初始化配置文件
		 */
		try(InputStream inputStream = CrawlerConstants.class.getResourceAsStream(PROP_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			Properties prop = new Properties();
			prop.load(inputStreamReader);
			
			stockInfoTargetUrl = prop.getProperty("stock.info.target.url");
			stockPriceTargetUrlPrefix = prop.getProperty("stock.price.target.url.prefix");
			stockPriceTargetShangzhengUrlSuffix = prop.getProperty("stock.price.target.shangzheng.url.suffix");
			stockPriceTargetShenzhengUrlSuffix = prop.getProperty("stock.price.target.shenzheng.url.suffix");
			
			stockEncoding = prop.getProperty("stock.encoding");
			
			shangzhengStockInfoCssSelector = prop.getProperty("stock.info.shangzheng.css.selector");
			shenzhengStockInfoCssSelector = prop.getProperty("stock.info.shenzheng.css.selector");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
