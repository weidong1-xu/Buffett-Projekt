package com.hanslv.crawler.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import com.hanslv.allgemein.dto.TabStockLabel;

/**
 * TabStockLabel Mapper
 * <p>
 * --------------------------------------------------------------
 * 1、删除全部信息												public void deleteAll()
 * 2、插入一条记录												public void insertOne(@Param("param")TabStockLabel param)
 * --------------------------------------------------------------
 *
 * @author hanslv
 */
@MapperScan
public interface TabStockLabelRepository {
    /**
     * 1、删除全部信息
     */
    @Delete("DELETE FROM tab_stock_label")
    public void deleteAll();

    /**
     * 2、插入一条记录
     *
     * @param param
     */
    @Insert("INSERT INTO tab_stock_label (sort_id , stock_id) VALUES (#{param.sortId} , #{param.stockId})")
    public void insertOne(@Param("param") TabStockLabel param);
}
