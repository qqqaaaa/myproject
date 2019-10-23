package com.microcyber.cloud.history;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * zhaiyunfeng:历史数据服务启动入口。本服务负责从Kafka中消费实时数据，并将数据持久化到Influxdb历史库中。
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.microcyber.cloud.history.mapper")
@EnableFeignClients //微服务调用时需要，调用其他微服务
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logger.info("--历史数据服务程序启动完成--");
	}

	@Configuration
	public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
		}
	}
}