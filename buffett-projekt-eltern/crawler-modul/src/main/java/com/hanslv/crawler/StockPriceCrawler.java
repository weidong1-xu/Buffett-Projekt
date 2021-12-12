package com.hanslv.crawler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hanslv.allgemein.constants.CommonsOtherConstants;
import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.crawler.constants.CrawlerConstants;
import com.hanslv.crawler.repository.TabStockInfoRepository;
import com.hanslv.crawler.util.CrawlerMessageTransUtil;
import com.hanslv.crawler.util.CrawlerUtil;

/**
 * 股票价格爬虫
 *
 * @author hanslv
 */
@Component
public class StockPriceCrawler {
    Logger logger = Logger.getLogger(StockPriceCrawler.class);
    /*
     * 全部股票基本信息List
     */
    private List<TabStockInfo> stockInfoList;

    /*
     * 原子计数器，每个线程通过这个数字判断从stockInfoList获取下一个股票基本信息的index
     */
    private AtomicInteger listIndexCounter;

    /*
     * 爬虫数据传输类
     */
    @Autowired
    private CrawlerMessageTransUtil messageTransUtil;


    @Autowired
    private TabStockInfoRepository stockInfoMapper;


    /**
     * 从全部股票基本信息List中获取一个股票信息，
     * 执行爬取逻辑并向消息队列中写入一个包含当前股票N天(在Properties文件中指定)价格信息的List
     */
    public void runCrawler() {
        /*
         * 初始化
         */
        init();


        /*
         * 从股票信息List中获取记录，
         * 当前下标小于List集合长度时继续执行
         */
        int currentIndex = 0;//获取当前股票信息List下标
        while ((currentIndex = listIndexCounter.getAndIncrement()) < stockInfoList.size()) {
            TabStockInfo stockInfo = stockInfoList.get(currentIndex);

            /*
             * 股票价格信息爬虫执行逻辑，执行爬虫获取当前股票的价格信息
             */
            JSONObject bodyTextJsonObject = stockPriceCrawlerLogic(stockInfo);

            /*
             * 首先判断当前返回的JSONObject是否为null，为空则不发送到Kafka
             */
            if (bodyTextJsonObject != null) {
                /*
                 * 将爬取回的JSONObject转换为List后，向KafkaUtil的消息队列中写入一个List<TabStockPriceInfo>
                 */
                messageTransUtil
                        .writePriceInfoToQueue(
                                parseJsonObjectToList(bodyTextJsonObject, stockInfo.getStockId()));
            }
        }

        /*
         * 放入消息结尾
         */
        messageTransUtil.writePriceInfoToQueue(new ArrayList<>());

        /*
         * 关闭MessageTransUtil中的线程池
         */
//		messageTransUtil.shudDownThreadPool();
    }


    /**
     * 爬取指定股票ID的股票信息
     *
     * @param stockIndex
     */
    public void runCrawler(int stockCode) {
        init();
        TabStockInfo stockInfo = stockInfoList.get(stockCode - 1);
        JSONObject bodyTextJsonObject = stockPriceCrawlerLogic(stockInfo);
        if (bodyTextJsonObject != null)
            messageTransUtil
                    .writePriceInfoToQueue(
                            parseJsonObjectToList(bodyTextJsonObject, stockInfo.getStockId()));
        messageTransUtil.writePriceInfoToQueue(new ArrayList<>());
    }


    /**
     * 初始化操作
     */
    private void init() {
        listIndexCounter = new AtomicInteger();
        /*
         * 初始化全部股票基本信息List
         */
        try {
            stockInfoList = Collections.synchronizedList(stockInfoMapper.selectAll());
            logger.info("共获取到了：" + stockInfoList.size() + "条股票基本信息");
            /*
             * 当获取到的股票基本信息数量为0时，考虑是否没有初始化股票基本信息表
             */
            if (stockInfoList.size() == 0)
                throw new Exception("股票基本信息获取失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过股票信息爬取到对应的价格信息并转换成JSONObject
     *
     * @return
     */
    private JSONObject stockPriceCrawlerLogic(TabStockInfo stockInfo) {
        logger.info(Thread.currentThread() + " 正在爬取股票：" + stockInfo.getStockName() + "，" + stockInfo.getStockCode() + "的信息");

        /*
         * 初始化爬取地址
         */
        String targetUrl = CrawlerConstants.stockPriceTargetUrlPrefix;
        String stockCode = stockInfo.getStockCode();

        /*
         * 首先判断是否为指数，之后拼接爬取地址
         */
        if (CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;//上证指数
        else if (CommonsOtherConstants.SHENZHENG_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//深证指数
        else if (CommonsOtherConstants.CHUANGYEBAN_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//创业板指数
        else if (stockCode.indexOf("6") != 0)
            targetUrl += stockCode + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//深证股票
        else targetUrl += stockCode + CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;//上证股票

        /*
         * 2019-12-19修改Bug，当股票不存在时跳过
         */
        Document document = CrawlerUtil.getInstance().getHttpResponse(targetUrl, CrawlerConstants.stockEncoding);
        if (document == null) return null;

        /*
         * 获取返回结果的字符串
         */
        StringBuffer contextStringBuffer = new StringBuffer(document.select("body").text());

        /*
         * 排除股票退市或其他情况
         */
        if (contextStringBuffer.length() != 0 && contextStringBuffer.indexOf("name") != -1) {
            /*
             * 爬虫取回的数据中包含了多余的报文，
             * 在此处除去多余部分
             */
            int subIndex = contextStringBuffer.indexOf("name") - 2;
            String finalContextStringBuffer = contextStringBuffer.substring(subIndex, contextStringBuffer.length() - 1);
            return JSONObject.parseObject(finalContextStringBuffer);
        } else return null;
    }


    /**
     * 将传入的JSONObject对象转换为TabStockPriceInfo的List
     *
     * @param stockPriceInfos
     * @return
     */
    private List<TabStockPriceInfo> parseJsonObjectToList(JSONObject stockPriceInfos, Integer stockCode) {
        List<TabStockPriceInfo> priceInfoList = new LinkedList<>();

        /**
         * 获取JSONObject中对应的JSONArray数组
         */
        JSONArray resultArray = stockPriceInfos.getJSONArray("data");

        /**
         * 遍历数组并将数据封装成TabStockPriceInfo对象
         */
        Iterator<Object> arrayIterator = resultArray.iterator();
        while (arrayIterator.hasNext()) {
            TabStockPriceInfo currentPriceInfo = new TabStockPriceInfo();
            String[] result = String.valueOf(arrayIterator.next()).split(",");

            /**
             * 跳过数据缺失的价格信息
             */
            if (result.length < 9) {
                logger.error("--------------股票" + stockCode + "在今天：" + new Date() + " 获取到数据有缺失，请检查数据---------------------");
                continue;
            }


            /**
             * 数据样本：2019-07-02,8.78,8.83,8.94,8.68,40466,35837123,2.96%,1.5
             * 日期： 2019-07-02,
             * 开盘价： 8.78,
             * 收盘价： 8.83,
             * 最高价： 8.94,
             * 最低价： 8.68,
             * 成交量： 40466,
             * 成交额： 35837123,
             * 振幅： 2.96%,
             * 换手率： 1.5
             */
            currentPriceInfo.setStockId(stockCode);
            currentPriceInfo.setStockPriceDate(result[0]);
            currentPriceInfo.setStockPriceStartPrice(new BigDecimal(result[1]));
            currentPriceInfo.setStockPriceEndPrice(new BigDecimal(result[2]));
            currentPriceInfo.setStockPriceHighestPrice(new BigDecimal(result[3]));
            currentPriceInfo.setStockPriceLowestPrice(new BigDecimal(result[4]));
            currentPriceInfo.setStockPriceVolume(new Integer(result[5]));
            String turnover = result[6].length() >= 5 ? result[6].substring(0, result[6].length() - 4) : "0";//万单位，当小于1万时为0
            currentPriceInfo.setStockPriceTurnover(new Integer(turnover));
            currentPriceInfo.setStockPriceAmplitude(result[7]);
            currentPriceInfo.setStockPriceTurnoverRate(new BigDecimal(result[8]));


            /**
             * 加入List中
             */
            priceInfoList.add(currentPriceInfo);
        }
        return priceInfoList;
    }

}
