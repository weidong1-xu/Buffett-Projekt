package com.hanslv.maschinelles.lernen.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hanslv.allgemein.dto.TabStockInfo;

/**
 * 股票信息Mapper
 * <p>
 * --------------------------------------------
 * 1、获取全部股票信息List							public List<TabStockInfo> selectAllStockInfo()
 * --------------------------------------------
 *
 * @author hanslv
 */
@Mapper
public interface TabStockInfoRepository {
    /**
     * 1、获取全部股票信息List
     *
     * @return
     */
    @Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info")
    public List<TabStockInfo> selectAllStockInfo();
}
