package com.hanslv.crawler.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.hanslv.allgemein.dto.TabStockSort;

/**
 * TabStockSort Mapper
 * <p>
 * --------------------------------------------------------------
 * 1、删除全部数据											public void deleteAll()
 * 2、插入一条记录											public void insertOne(@Param("param")TabStockSort param)
 * --------------------------------------------------------------
 *
 * @author hanslv
 */
@Mapper
public interface TabStockSortRepository {
    /**
     * 1、删除全部数据
     */
    @Delete("DELETE FROM tab_stock_sort")
    public void deleteAll();


    /**
     * 2、插入一条记录
     *
     * @param param
     */
    @Insert("INSERT INTO tab_stock_sort (sort_name , sort_code) VALUES (#{param.sortName} , #{param.sortCode})")
    @Options(useGeneratedKeys = true, keyProperty = "param.sortId", keyColumn = "sort_id")
    public void insertOne(@Param("param") TabStockSort param);
}
