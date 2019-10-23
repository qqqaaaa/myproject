package com.microcyber.cloud.history.kafka;

import com.microcyber.cloud.history.web.RawDataListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * kafka消费者配置
 * @author zhaohongwei
 *
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${kafka.consumer.servers}")
    private String servers;
    @Value("${kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${kafka.consumer.session-timeout}")
    private String sessionTimeout;
    @Value("${kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;
    @Value("${kafka.consumer.groupId}")
    private String groupId;
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.concurrency}")
    private int concurrency;
    @Value("${kafka.consumer.max-poll-records}")
    private int maxPollRecords;
    @Value("${kafka.consumer.max-partition-fetch-bytes}")
    private int maxPartitionFetchBytes;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //多线程消费kafka
        factory.setConcurrency(concurrency);
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        propsMap.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        return propsMap;
    }
    /**
     * kafka监听
     * @return
     */
    @Bean
    public RawDataListener listener() {
        logger.info("==创建一个RawDataListener监听器==");
        return new RawDataListener();
    }
}