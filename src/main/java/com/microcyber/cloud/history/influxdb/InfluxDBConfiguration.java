package com.microcyber.cloud.history.influxdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author zhaohongwei
 *
 */
@Configuration
public class InfluxDBConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBConfiguration.class);

    @Value("${influxdb.client.username}")
    private String username;
    @Value("${influxdb.client.password}")
    private String password;
    @Value("${influxdb.client.url}")
    private String url;
    @Value("${influxdb.client.database}")
    private String database;


    @Bean
    public InfluxDBClient getInfluxDBConnect(){
        InfluxDBClient influxDB = new InfluxDBClient(username, password, url, database);
        influxDB.influxDbBuild();
        //30天的存储策略，这里要根据实际情况调整，不确定的话，注释掉，Influxdb默认策略是不删除。
        influxDB.createRetentionPolicy();
        logger.info("==创建一个InfluxDBClient客户端==");
        return influxDB;
    }
}