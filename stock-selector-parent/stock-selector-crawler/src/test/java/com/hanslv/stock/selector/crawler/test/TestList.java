package com.hanslv.stock.selector.crawler.test;

import java.util.ArrayList;
import java.util.List;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;

public class TestList {
	public static void main(String[] args) {
		TabStockInfo paramA = new TabStockInfo();
		paramA.setStockId(1);
		paramA.setStockCode("test001");
		TabStockInfo paramB = new TabStockInfo();
		paramB.setStockCode("test001");
		
		List<TabStockInfo> lista = new ArrayList<>();
		lista.add(paramA);
		
		for(TabStockInfo a : lista) a.setStockId(null);
		
		List<TabStockInfo> listb = new ArrayList<>();
		listb.add(paramB);
		
		listb.removeAll(lista);
		
		System.out.println(listb);
		
	}
}
