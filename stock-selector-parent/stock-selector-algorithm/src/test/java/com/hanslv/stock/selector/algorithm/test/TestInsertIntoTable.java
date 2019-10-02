package com.hanslv.stock.selector.algorithm.test;

import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

public class TestInsertIntoTable {
	public static void main(String[] args) {
		TabStockPriceInfoRepository mapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
		TabStockPriceInfo info = new TabStockPriceInfo();
		info.setStockId(1);
		info.setStockPriceDate("2019-10-03");
		mapper.insertOne("tab_stock_price_shenzheng_0003" , info);
		MyBatisUtil.getInstance().commitConnection();
	}
}
