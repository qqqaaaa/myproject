package com.microcyber.cloud.history.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.microcyber.cloud.history.model.DataItem;
import com.microcyber.cloud.history.service.DataItemService;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class DataItemScheduleTask {
	public  static  Map data_item_names = new HashMap();
	public  static  Map data_types = new HashMap();;
	public  static  Map data_save_db = new HashMap();;
	@Autowired
	private DataItemService dataItemService;
    @Scheduled(fixedRate=1000*50)
    private void configureTasks() {
    	List<DataItem> dataItemList = dataItemService.getList();
    	for(int i=0;i<dataItemList.size();i++) {
    		Long dataItemId=dataItemList.get(i).getDataItemId();
    		String dataItemName=dataItemList.get(i).getDataItemName();
    		int dataType=dataItemList.get(i).getDataType();
    		data_item_names.put(String.valueOf(dataItemId), dataItemName);
    	}
    	for(int i=0;i<dataItemList.size();i++) {
    		Long dataItemId=dataItemList.get(i).getDataItemId();
    		int dataType=dataItemList.get(i).getDataType();
    		data_types.put(String.valueOf(dataItemId), dataType);
    	}
    	for(int i=0;i<dataItemList.size();i++) {
    		Long dataItemId=dataItemList.get(i).getDataItemId();
    		int save_db=dataItemList.get(i).getPersist();
    		data_save_db.put(String.valueOf(dataItemId), save_db);
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.err.println("执行静态定时任务时间: " + sdf.format(new Date()));
    }
}
