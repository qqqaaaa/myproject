package com.microcyber.cloud.history.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcyber.cloud.history.influxdb.InfluxDBClient;
import com.microcyber.cloud.history.model.KafkaModel;
import com.microcyber.cloud.history.task.DataItemScheduleTask;

/**
 * kafka监听程序。多个历史服务实例，需配置相同的消费组，保证多个实例不会同时消费数据。
 * 采集服务写入到Kafka是一个Topic,历史数据服务与采集服务保持一致的Topic名称。
 *
 * @author zhaohongwei
 */
@Component
public class RawDataListener {
    private static final Logger logger = LoggerFactory.getLogger(RawDataListener.class);
    @Autowired
    private InfluxDBClient influxDB;
    @Autowired
    private ObjectMapper mapper;
    /**
     * 实时获取kafka数据(生产一条，监听生产topic自动消费一条)
     *
     * @param record
     */
//    @KafkaListener(topics = {"${kafka.consumer.topic}"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        String value = (String) record.value();
//        if (StringUtils.isBlank(value)) return;
//        KafkaModel data;
//        try {
//        	System.out.println("recv="+value);
//            data = mapper.readValue(value, KafkaModel.class);
//            String data_types = DataItemScheduleTask.data_types;
//	       	JSONObject json_data_types = JSONObject.parseObject(data_types);
//	        String data_save_db = DataItemScheduleTask.data_save_db;
//	       	JSONObject json_data_save = JSONObject.parseObject(data_save_db);
//            influxDB.batchInsert(data,json_data_types,json_data_save);
//        } catch (Exception ex) {
//            logger.error("Kafka订阅数据存储失败：" + ex.getMessage());
//            logger.error("原始数据：" + value);
//        }
//    }
    /**批量处理kafka消息
     * @param records
     */
    @KafkaListener(topics = {"${kafka.consumer.topic}"})
    public void listen(List<ConsumerRecord<?, ?>> records) {
        logger.info("订阅数据：" + records.size());
    	List<KafkaModel> datas  = new ArrayList<KafkaModel>();
    	try {
	    	for (ConsumerRecord<?,?> record : records) {
	    		String value = (String) record.value();
	    		if (StringUtils.isBlank(value)) return;
	            KafkaModel data;
	            data = mapper.readValue(value, KafkaModel.class);
	            datas.add(data);
	    	}
            influxDB.batchInsert(datas,DataItemScheduleTask.data_types,DataItemScheduleTask.data_save_db);
        } catch (Exception ex) {
            logger.error("Kafka订阅数据存储失败：" + ex.getMessage());
            logger.error("原始数据：" + datas.toString());
        }

    }
    
}