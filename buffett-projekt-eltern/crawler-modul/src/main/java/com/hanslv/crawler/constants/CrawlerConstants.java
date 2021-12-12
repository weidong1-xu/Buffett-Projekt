package com.hanslv.crawler.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jboss.logging.Logger;


/**
 * 爬虫常量类
 * ------------------------------------------
 * <p>
 * ------------------------------------------
 *
 * @author harrylu
 */
public abstract class CrawlerConstants {
    static Logger logger = Logger.getLogger(CrawlerConstants.class);
    private static final String PROP_PATH = "/crawler.properties";//配置文件URL

    public static String stockInfoTargetUrl;//股票基本信息爬取目标地址

    public static String stockPriceTargetUrlPrefix;//股票价格信息爬取目标地址前缀
    public static String stockPriceTargetShangzhengUrlSuffix;//上证股票价格信息爬取目标地址后缀
    public static String stockPriceTargetShenzhengUrlSuffix;//深证股票价格信息爬取目标地址后缀

    public static String stockEncoding;//股票返回信息编码格式

    public static String shangzhengStockInfoCssSelector;//上证股票信息CSS选择器
    public static String shenzhengStockInfoCssSelector;//深证股票信息CSS选择器


    public static final String PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX = "tab_stock_price_shangzheng_";//价格信息分表上证表名称前缀
    public static final String PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX = "tab_stock_price_shenzheng_";//价格信息分表深证表名称前缀
    public static final int STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE = 10;//插入股票价格信息的线程池大小
    public static final int PRICE_INFO_MESSAGE_QUEUE_SIZE = 10;


    public static String sortMainUrlPrefix;//获取全部股票分类URL前缀
    public static String sortMainUrlClassSuffix;//概念分类URL后缀
    public static String sortMainUrlareaSuffix;//地区分类URL后缀
    public static String sortMainUrlIndustrySuffix;//行业分类URL后缀
    public static String sortMainUrlOther;//其他分类URL
    public static String sortClassBodyExcludeString;//概念板块需要排除的返回信息
    public static String sortAreaBodyExcludeString;//地区板块需要排除的返回信息
    public static String sortIndustryBodyExcludeString;//行业板块需要排除的返回信息
    public static String sortOtherBodyExcludeString;//其他板块需要排除的返回信息

    public static String sortMainNameSplit;//回的分类信息分割符号
    public static Integer sortMainNameIndex;//返回的分类信息所在数组中位置

    public static String sortStockLabelUrlPrefix;//板块包含股票页面地址前缀
    public static String sortStockLabelUrlSuffix;//板块包含股票页面地址后缀
    public static String sortStockLabelKeyName;//板块所包含股票在返回信息中的key

    public static String sortEncoding;//返回信息编码格式

    public static int sortSleepSecond;//获取板块休眠时间

    static {
        /**
         * 初始化配置文件
         */
        try (InputStream inputStream = CrawlerConstants.class.getResourceAsStream(PROP_PATH);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {
            Properties prop = new Properties();
            prop.load(inputStreamReader);

            stockInfoTargetUrl = prop.getProperty("stock.info.target.url");
            stockPriceTargetUrlPrefix = prop.getProperty("stock.price.target.url.prefix");
            stockPriceTargetShangzhengUrlSuffix = prop.getProperty("stock.price.target.shangzheng.url.suffix");
            stockPriceTargetShenzhengUrlSuffix = prop.getProperty("stock.price.target.shenzheng.url.suffix");

            stockEncoding = prop.getProperty("stock.encoding");

            shangzhengStockInfoCssSelector = prop.getProperty("stock.info.shangzheng.css.selector");
            shenzhengStockInfoCssSelector = prop.getProperty("stock.info.shenzheng.css.selector");

            sortMainUrlPrefix = prop.getProperty("sort.main.url.prefix");//获取全部股票分类URL前缀
            sortMainUrlClassSuffix = prop.getProperty("sort.main.url.class.suffix");//概念分类URL后缀
            sortMainUrlareaSuffix = prop.getProperty("sort.main.url.area.suffix");//地区分类URL后缀
            sortMainUrlIndustrySuffix = prop.getProperty("sort.main.url.industry.suffix");//行业分类URL后缀
            sortMainUrlOther = prop.getProperty("sort.main.url.other");//其他分类URL
            sortClassBodyExcludeString = prop.getProperty("sort.class.body.exclude.string");//概念板块需要排除的返回信息
            sortAreaBodyExcludeString = prop.getProperty("sort.area.body.exclude.string");//地区板块需要排除的返回信息
            sortIndustryBodyExcludeString = prop.getProperty("sort.industry.body.exclude.string");//行业板块需要排除的返回信息
            sortOtherBodyExcludeString = prop.getProperty("sort.other.body.exclude.string");//其他板块需要排除的返回信息

            sortMainNameSplit = prop.getProperty("sort.main.name.split");//回的分类信息分割符号
            sortMainNameIndex = Integer.parseInt(prop.getProperty("sort.main.name.index"));//返回的分类信息所在数组中位置

            sortStockLabelUrlPrefix = prop.getProperty("sort.stock.label.url.prefix");//板块包含股票页面地址前缀
            sortStockLabelUrlSuffix = prop.getProperty("sort.stock.label.url.suffix");//板块包含股票页面地址后缀
            sortStockLabelKeyName = prop.getProperty("sort.stock.label.key.name");//板块所包含股票在返回信息中的key

            sortEncoding = prop.getProperty("sort.encoding");//返回信息编码格式

            sortSleepSecond = Integer.parseInt(prop.getProperty("sort.sleep.second"));//获取板块休眠时间

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
