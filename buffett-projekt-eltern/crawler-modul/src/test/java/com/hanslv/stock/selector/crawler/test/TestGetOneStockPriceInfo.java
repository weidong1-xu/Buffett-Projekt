package com.hanslv.stock.selector.crawler.test;

import java.util.Iterator;

import org.jboss.logging.Logger;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hanslv.crawler.constants.CrawlerConstants;
import com.hanslv.crawler.util.CrawlerUtil;

public class TestGetOneStockPriceInfo {
    Logger logger = Logger.getLogger(TestGetOneStockPriceInfo.class);

    private static final String STOCK_CODE = "600030";

    @Test
    public void testGetOneStockPriceInfo() {
        JSONArray resultArray = getJsonObject(STOCK_CODE).getJSONArray("data");
        Iterator<Object> arrayIterator = resultArray.iterator();
        while (arrayIterator.hasNext()) {
            String result =
                    String
                            .valueOf(arrayIterator.next());
//						.replaceAll("[", "")
//						.replaceAll("]", "")
//						.split(",")[2];
            logger.info(result);
        }
    }


    /**
     * 通过股票信息爬取到对应的价格信息并转换成JSONObject
     *
     * @return
     */
    private JSONObject getJsonObject(String stockCode) {
        String targetUrl = CrawlerConstants.stockPriceTargetUrlPrefix + stockCode;//爬取目标地址
        /**
         * 判断是否为上证股票
         */
        if (stockCode.indexOf("6") != 0)
            targetUrl += CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;
        else
            targetUrl += CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;

//		logger.info(context);


        StringBuffer contextStringBuffer = new StringBuffer(
                CrawlerUtil
                        .getInstance()
                        .getHttpResponse(targetUrl, CrawlerConstants.stockEncoding)
                        .select("body")
                        .text());
        int subIndex = contextStringBuffer.indexOf("name") - 2;
        String finalContextStringBuffer = contextStringBuffer.substring(subIndex, contextStringBuffer.length() - 1);
        return JSONObject.parseObject(finalContextStringBuffer);
    }
}
