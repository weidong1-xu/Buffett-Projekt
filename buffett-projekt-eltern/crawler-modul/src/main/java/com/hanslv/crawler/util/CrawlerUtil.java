package com.hanslv.crawler.util;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 爬虫工具类（单利）
 * 实例化的PoolingHttpClientConnectionManager不会被关闭
 * ---------------------------------------
 * 1、执行一个HttpGet请求								public Document getHttpResponse(String targetUrl , String encodingType)
 * 2、获取Element中的Text								public String getTextFromElement(Element element)
 * ---------------------------------------
 *
 * @author harrylu
 */
public class CrawlerUtil {
    private static class Singleton {
        private static final CrawlerUtil INSTANCE = new CrawlerUtil();
    }

    /**
     * 1、获取单利CrawlerUtil对象
     *
     * @return
     */
    public static CrawlerUtil getInstance() {
        return Singleton.INSTANCE;
    }

    Logger logger = Logger.getLogger(CrawlerUtil.class);
    private PoolingHttpClientConnectionManager connectionManager;//连接管理器


    private CrawlerUtil() {
        /**
         * 实例化连接管理器
         * 1、实例化http请求Factory
         * 2、实例化https请求Factory
         * 3、实例化注册器
         * 4、实例化连接管理器并设置http请求Factory、https请求Factory、注册器
         */
        ConnectionSocketFactory httpSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        SSLConnectionSocketFactory httpsSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", httpSocketFactory)
                .register("https", httpsSocketFactory)
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(registry);
    }


    /**
     * 1、执行一个HttpGet请求
     *
     * @param targetUrl    请求目标地址
     * @param encodingType 编码格式
     * @return
     */
    public Document getHttpResponse(String targetUrl, String encodingType) {
        HttpGet getRequest = new HttpGet(targetUrl);
        try {
            return Jsoup.parse(
                    EntityUtils.toString(
                            HttpClients.custom().setConnectionManager(connectionManager).build().execute(getRequest).getEntity(), encodingType));
        } catch (IOException e) {
            return null;
        } finally {
            /**
             * 释放连接
             */
            if (getRequest != null)
                getRequest.releaseConnection();
        }
    }


    /**
     * 2、获取Element中的Text
     *
     * @param element
     * @return
     */
    public String getTextFromElement(Element element) {
        String result = element.text();
        return result;
    }

}
