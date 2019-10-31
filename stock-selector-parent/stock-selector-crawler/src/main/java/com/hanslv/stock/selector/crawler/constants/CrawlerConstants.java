package com.hanslv.stock.selector.crawler.constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.logging.Logger;


/**
 * 爬虫常量类
 * ------------------------------------------
 * 1、#股票基本信息爬取目标地址前缀												stockInfoTargetUrlPrefix
 * 2、#股票基本信息爬取目标地址后缀												stockInfoTargetUrlSuffix
 * 3、股票基本信息爬取目标地址													stockInfoTargetUrl
 * 4、股票价格信息爬取目标地址前缀												stockPriceTargetUrlPrefix
 * 5、上证股票价格信息爬取目标地址后缀											stockPriceTargetShangzhengUrlSuffix
 * 6、深证股票价格信息爬取目标地址后缀											stockPriceTargetShenzhengUrlSuffix
 * 7、股票返回信息编码格式														stockEncoding
 * 8、上证股票信息CSS选择器														shangzhengStockInfoCssSelector
 * 9、深证股票信息CSS选择器														shenzhengStockInfoCssSelector
 * ------------------------------------------
 * @author harrylu
 *
 */
public abstract class CrawlerConstants {
	static Logger logger = Logger.getLogger(CrawlerConstants.class);
	private static final String BASE_PATH = CrawlerConstants.class.getClassLoader().getResource("").toString().replaceAll("file:/", "");//项目根目录
	private static final String PROP_PATH = BASE_PATH + "crawler.properties";//配置文件URL
	private static Properties prop;//Properties文件对象

	
//	public static String stockInfoTargetUrlPrefix;//股票基本信息爬取目标地址前缀
//	public static String stockInfoTargetUrlSuffix;//股票基本信息爬取目标地址后缀
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
	
	public static final String ALGORITHM_RESULT_TYPE_UNKNOWN = "UNKNOWN";//股票算法结果状态-UNKNOWN
	public static final String ALGORITHM_RESULT_TYPE_SUCCESS = "SUCCESS";//股票算法结果状态-SUCCESS
	public static final String ALGORITHM_RESULT_TYPE_FAIL = "FAIL";//股票算法结果状态-FAIL
	
	
	static {
		/**
		 * 初始化配置文件
		 */
		prop = new Properties();
		try(FileInputStream fileInputStream = new FileInputStream(PROP_PATH);){
			prop.load(fileInputStream);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
//		stockInfoTargetUrlPrefix = prop.getProperty("stock.info.target.url.prefix");
//		stockInfoTargetUrlSuffix = prop.getProperty("stock.info.target.url.suffix");
		stockInfoTargetUrl = prop.getProperty("stock.info.target.url");
		stockPriceTargetUrlPrefix = prop.getProperty("stock.price.target.url.prefix");
		stockPriceTargetShangzhengUrlSuffix = prop.getProperty("stock.price.target.shangzheng.url.suffix");
		stockPriceTargetShenzhengUrlSuffix = prop.getProperty("stock.price.target.shenzheng.url.suffix");
		
		stockEncoding = prop.getProperty("stock.encoding");
		
		shangzhengStockInfoCssSelector = prop.getProperty("stock.info.shangzheng.css.selector");
		shenzhengStockInfoCssSelector = prop.getProperty("stock.info.shenzheng.css.selector");
		
	}
}
