package com.hanslv.stock.selector.commons.kafka.deserializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.alibaba.fastjson.JSON;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * TabStockPriceInfo反序列化
 * @author hanslv
 *
 */
public class TabStockPriceInfoDeserializer implements Deserializer<TabStockPriceInfo>{

	@Override
	public void close() {
		
	}

	@Override
	public void configure(Map<String, ?> configs , boolean isKey) {
		
	}

	@Override
	public TabStockPriceInfo deserialize(String topic , byte[] priceInfoBytes) {
		return priceInfoBytes == null ? null : JSON.parseObject(priceInfoBytes , TabStockPriceInfo.class);
	}
	
}
