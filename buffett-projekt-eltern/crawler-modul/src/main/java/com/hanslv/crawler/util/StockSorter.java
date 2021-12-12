package com.hanslv.crawler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.allgemein.dto.TabStockLabel;
import com.hanslv.allgemein.dto.TabStockSort;
import com.hanslv.crawler.constants.CrawlerConstants;
import com.hanslv.crawler.repository.TabStockInfoRepository;
import com.hanslv.crawler.repository.TabStockLabelRepository;
import com.hanslv.crawler.repository.TabStockSortRepository;

/**
 * 股票分类器
 * <p>
 * SELECT info.stock_code , info.stock_name , sort.sort_name FROM
 * tab_stock_info info LEFT JOIN tab_stock_label label ON info.stock_id = label.stock_id
 * LEFT JOIN tab_stock_sort sort ON label.sort_id = sort.sort_id
 * WHERE sort.sort_name LIKE '%其他金融%';
 *
 * @author hanslv
 */
@Component
public class StockSorter {
    Logger logger = Logger.getLogger(StockSorter.class);

    @Autowired
    private TabStockLabelRepository stockLabelMapper;

    @Autowired
    private TabStockSortRepository stockSortMapper;

    @Autowired
    private TabStockInfoRepository stockInfoMapper;

    private CrawlerUtil crawlerUtil = CrawlerUtil.getInstance();

    /*
     * 1、执行股票分类
     * 步骤：
     * 1、从网页获取全部股票分类（板块）
     * 2、清空股票分类表信息、标签表信息
     * 3、将股票分类（板块）插入到股票分类表中
     * 4、获取当前全部股票信息（股票名称、股票code、股票ID）
     * 5、获取全部股票分类（板块），根据URL获取分类（板块）下的股票
     * 6、判断当前股票信息表中是否存在该股票，若存在则将信息存入股票标签表中（股票ID、标签ID）
     */
    public void doSort() {
        /*
         * 获取全部股票信息
         */
        List<TabStockInfo> stockInfoList = stockInfoMapper.selectAll();
        Map<String, TabStockInfo> stockInfoMap = new HashMap<>();
        for (TabStockInfo stockInfo : stockInfoList) stockInfoMap.put(stockInfo.getStockCode(), stockInfo);

        Map<String, String> sortMap = new HashMap<>();

        /*
         * 从网页获取全部股票分类
         */
        boolean resultFlag = false;
        resultFlag = getSort(CrawlerConstants.sortMainUrlClassSuffix, CrawlerConstants.sortClassBodyExcludeString, sortMap, false);//概念板块
        resultFlag = getSort(CrawlerConstants.sortMainUrlareaSuffix, CrawlerConstants.sortAreaBodyExcludeString, sortMap, false);//地区板块
        resultFlag = getSort(CrawlerConstants.sortMainUrlIndustrySuffix, CrawlerConstants.sortIndustryBodyExcludeString, sortMap, false);//行业板块
        resultFlag = getSort(CrawlerConstants.sortMainUrlOther, CrawlerConstants.sortOtherBodyExcludeString, sortMap, true);//其他板块
        if (!resultFlag) {
            logger.error("板块信息获取失败！！！！");
            return;
        }


        /*
         * 删除全部旧分类标签信息、分类信息
         */
        stockLabelMapper.deleteAll();
        stockSortMapper.deleteAll();


        /*
         * 遍历获取到的分类信息
         */
        for (Entry<String, String> objEntry : sortMap.entrySet()) {
            String sortCode = objEntry.getKey();
            String sortName = objEntry.getValue().split(CrawlerConstants.sortMainNameSplit)[CrawlerConstants.sortMainNameIndex];
            if ("".equals(sortCode) || "".equals(sortName)) continue;
            TabStockSort stockSort = new TabStockSort();
            stockSort.setSortCode(sortCode);
            stockSort.setSortName(sortName);

            /*
             * 将当前分类插入分类信息表
             */
            stockSortMapper.insertOne(stockSort);
            int sortId = stockSort.getSortId();//返回的自增主键

            /*
             * 根据板块Code获取当前板块包含的股票
             */
            List<String> sortStockList = getSortStock(sortCode);
            if (sortStockList == null) continue;
            for (String sortStock : sortStockList) {
                /*
                 * 判断当前股票信息表中是否存在该股票
                 */
                TabStockInfo stockInfo = stockInfoMap.get(sortStock);
                if (stockInfo != null) {
                    TabStockLabel stockLabel = new TabStockLabel();
                    stockLabel.setStockId(stockInfo.getStockId());
                    stockLabel.setSortId(sortId);
                    stockLabelMapper.insertOne(stockLabel);
                } else logger.error("当前股票不存在：" + sortStock + "，板块名称：" + sortName);
            }
            logger.info(stockSort + "板块信息获取完毕");

            try {
                TimeUnit.SECONDS.sleep(CrawlerConstants.sortSleepSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("------------------------------全部板块信息获取完毕------------------------------");
    }


    /**
     * 根据所给URL后缀获取板块信息
     *
     * @param urlSuffix
     * @param excludeStrng
     * @param sortMap
     * @param other        其他行业url拼接方式与普通不同
     */
    private boolean getSort(String urlSuffix, String excludeStrng, Map<String, String> sortMap, boolean other) {
        String url = other ? urlSuffix : CrawlerConstants.sortMainUrlPrefix + urlSuffix;
        Document sortDocument = crawlerUtil.getHttpResponse(url, CrawlerConstants.sortEncoding);
        if (sortDocument == null) return false;//当返回为null则中断
        String sortBody = sortDocument.select("body").text().replaceAll(excludeStrng, "");//获取body内容并替换掉前缀
        sortMap.putAll(JSON.parseObject(sortBody, new TypeReference<Map<String, String>>() {
        }));//将返回的JSON字符串解析成Map
        return true;
    }

    /*
     * 获取当前分类下包含的全部股票
     */
    private List<String> getSortStock(String stockLabelUrlMiddle) {
        List<String> stockCodeList = new ArrayList<>();
        Document sortStockDocument = crawlerUtil.getHttpResponse(CrawlerConstants.sortStockLabelUrlPrefix + stockLabelUrlMiddle + CrawlerConstants.sortStockLabelUrlSuffix, CrawlerConstants.sortEncoding);
        String sortStockBody = sortStockDocument.select("body").text();//获取body信息
        JSONArray sortStockArray = null;
        try {
            sortStockArray = JSON.parseArray(sortStockBody);//转换为JSONArray
        } catch (Exception e) {
            logger.error("解析JSON发生错误：" + stockLabelUrlMiddle);
            logger.error(sortStockBody);
            return null;
        }
        for (int i = 0; i < sortStockArray.size(); i++) {
            JSONObject stockObject = JSON.parseObject(String.valueOf(sortStockArray.get(i)));
            stockCodeList.add(stockObject.getString(CrawlerConstants.sortStockLabelKeyName));//获取JSONObject中信息并添加到List中
        }
        return stockCodeList;
    }
}
