package com.hanslv.stock.selector.commons.kafka.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.alibaba.fastjson.JSON;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * TabStockPriceInfo序列化器
 * @author hanslv
 *
 */
public class TabStockPriceInfoSerializer implements Serializer<TabStockPriceInfo>{
	
	@Override
	public void close() {
		
	}

	@Override
	public void configure(Map<String, ?> configs , boolean isKey) {
		
	}

	/**
	 * 执行序列化
	 */
	@Override
	public byte[] serialize(String topic, TabStockPriceInfo currentPriceInfo) {
		return currentPriceInfo == null ? null : JSON.toJSONBytes(currentPriceInfo);
	}

}
