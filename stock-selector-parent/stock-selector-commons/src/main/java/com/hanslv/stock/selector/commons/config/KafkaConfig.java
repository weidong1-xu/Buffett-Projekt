package com.hanslv.stock.selector.commons.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.kafka.deserializer.TabStockPriceInfoDeserializer;

/**
 * Kafka配置类
 * @author hanslv
 *
 */
@Configuration
@EnableKafka
public class KafkaConfig {
	/**
	 * 股票价格信息Kafka消费者监听器
	 * @return
	 */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TabStockPriceInfo>> stockPriceInfoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TabStockPriceInfo> factory = new ConcurrentKafkaListenerContainerFactory<String, TabStockPriceInfo>();
        factory.setConsumerFactory(priceInfoConsumerFactory());
        factory.setConcurrency(2);
        factory.getContainerProperties().setPollTimeout(4000);
        return factory;
    }
	
	/**
	 * 股票价格信息消费者工厂
	 * @return
	 */
    private ConsumerFactory<String, TabStockPriceInfo> priceInfoConsumerFactory() {
         return new DefaultKafkaConsumerFactory<String, TabStockPriceInfo>(priceInfoConsumerConfig());
    }
	
	/**
	 * 股票价格信息消费者配置文件
	 * @return
	 */
	private Map<String , Object> priceInfoConsumerConfig(){
		Map<String , Object> properties = basicProperties();
		properties.put(ConsumerConfig.GROUP_ID_CONFIG , CommonsKafkaConstants.PRICE_INFO_CONSUMER_GROUP);
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TabStockPriceInfoDeserializer.class);
		return properties;
	}
	
	

	
//	/**
//	 * 测试
//	 * @return
//	 */
//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> testKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
//        factory.setConsumerFactory(testConsumerFactory());
//        factory.setConcurrency(2);
//        factory.getContainerProperties().setPollTimeout(4000);
//        return factory;
//    }
//	
//	/**
//	 * 测试
//	 * @return
//	 */
//    private ConsumerFactory<String, String> testConsumerFactory() {
//         return new DefaultKafkaConsumerFactory<String, String>(testConsumerConfig());
//    }
//	
//	/**
//	 * 测试
//	 * @return
//	 */
//	private Map<String , Object>testConsumerConfig(){
//		Map<String , Object> properties = basicProperties();
//		properties.put(ConsumerConfig.GROUP_ID_CONFIG , "");
//		return properties;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 基本配置文件，需要设置消费者组
	 * @return
	 */
	private Map<String , Object> basicProperties(){
		Map<String , Object> basicProperties = new HashMap<>();

		basicProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.55:9092");
		basicProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		basicProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		basicProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		basicProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		return basicProperties;
	}
}
