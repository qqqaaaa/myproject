package com.microcyber.cloud.history.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class MimicDataTask {
	long count = 0;
    //@Scheduled(fixedRate=1000*60*2)
    private void configureTasks() {
		int a = 0;
		InfluxDBClientTest influx=new InfluxDBClientTest("root","123456","http://192.168.2.196:8086","testRp");
		influx.influxDbBuild();
		List<Map> lm = new ArrayList<Map>();
		String value = "你好中国";
		String value15 = "15";
		for (int i = 0;i<5000;i++){
			a++;
			Map m = new HashMap();
			m.put("id",new Random().nextInt(100));
			m.put("room",new Random().nextInt(100));
			m.put("type",new Random().nextInt(100));
			m.put("value1",value+i);
			m.put("value2",i);
			m.put("value3",value15+i/100/100);
			m.put("value4",value15+i/100/50);
			m.put("value5",value15+i/100/40);
			m.put("value6",value15+i/100/30);
			lm.add(m);
			if(a%10==0){
				try {
					influx.batchInsert(lm,"countData");
					lm.clear();
					a = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.err.println("执行静态定时任务时间: " + sdf.format(new Date()));
    }
	//@Scheduled(fixedRate=1000*5)//模拟设备上传
	private void configureTasks1() {
		int a = 0;
		InfluxDBClientTest influx=new InfluxDBClientTest("root","123456","http://192.168.2.196:8086","history");
		influx.influxDbBuild();
		List<Map> lm = new ArrayList<Map>();
		for (int i = 0;i<1000;i++){
			Long deviceId=10000000+Long.valueOf(i);
			for(int j = 0 ;j<100;j++){
				int c = 1;
				if(j>25&&j<50){
					c =2;
				}else if(j>=50&&j<75){
					c=3;
				}else if(j>=75){
					c=4;
				}
				a++;
				Map m = new HashMap();
				m.put("id",100*i+j);
				m.put("name","你好"+j);
				m.put("type",c);
				m.put("value",new Random().nextInt(1000));
				m.put("deviceId",deviceId);
				lm.add(m);
				try {
					if(a%10000==0) {
						influx.batchInsert1(lm);
						lm.clear();
						a = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		count = count + 100000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("执行静态定时任务时间: " + sdf.format(new Date())+"count:"+count);
	}
}
