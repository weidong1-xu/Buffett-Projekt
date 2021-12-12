package com.hanslv.stock.selector.crawler.test;

import com.hanslv.allgemein.constants.CommonsOtherConstants;
import com.hanslv.crawler.constants.CrawlerConstants;

public class TestStockUrlChoose {
    public static void main(String[] args) {
        System.out.println(check("000001"));
    }


    private static String check(String stockCode) {
        String targetUrl = CrawlerConstants.stockPriceTargetUrlPrefix;
        if (CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;//上证指数
        else if (CommonsOtherConstants.SHENZHENG_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//深证指数
        else if (CommonsOtherConstants.CHUANGYEBAN_ZHISHU_STOCK_CODE.equals(stockCode))
            targetUrl += stockCode.replaceAll("\\.", "") + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//创业板指数
        else if (stockCode.indexOf("6") != 0)
            targetUrl += stockCode + CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;//深证股票
        else targetUrl += stockCode + CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;//上证股票
        return targetUrl;
    }
}
