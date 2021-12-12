package com.hanslv.crawler.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.allgemein.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * <p>
 * ----------------------------------------------
 * 2、插入一条数据											public void insertOne(@Param("tableName")String tableName , @Param("priceInfo")TabStockPriceInfo priceInfo)
 * 3、获取一条数据											public TabStockPriceInfo selectOne(@Param("tableName") String tableName , @Param("priceInfo") TabStockPriceInfo currentPriceInfo)
 * ----------------------------------------------
 *
 * @author harrylu
 */
@Mapper
public interface TabStockPriceInfoRepository {

    /**
     * 2、插入一条数据
     *
     * @param priceInfo
     */
    public void insertOne(@Param("tableName") String tableName, @Param("priceInfo") TabStockPriceInfo priceInfo);


    /**
     * 3、获取一条数据
     *
     * @param currentPriceInfo
     * @return
     */
    @Select("SELECT "
            + "stock_id , "
            + "stock_price_date , "
            + "stock_price_start_price ,  "
            + "stock_price_end_price , "
            + "stock_price_highest_price , "
            + "stock_price_lowest_price , "
            + "stock_price_volume , "
            + "stock_price_turnover , "
            + "stock_price_amplitude , "
            + "stock_price_turnover_rate "
            + "FROM ${tableName} WHERE stock_id = #{priceInfo.stockId} AND stock_price_date = #{priceInfo.stockPriceDate}")
    public TabStockPriceInfo selectOne(@Param("tableName") String tableName, @Param("priceInfo") TabStockPriceInfo currentPriceInfo);
}
