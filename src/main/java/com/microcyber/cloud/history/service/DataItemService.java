package com.microcyber.cloud.history.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microcyber.cloud.history.mapper.DataItemDAO;
import com.microcyber.cloud.history.model.DataItem;

/**
 * @author zhaohongwei
 */
@Service
public class DataItemService {

    @Autowired
    private DataItemDAO dataItemMapper;

	public List<DataItem> getList(){
		return dataItemMapper.getList();
	}
	public int batchInsert(List<Map> lm){
		return dataItemMapper.batchInsert(lm);
	}
	public int insertDevice(Map map){
		return dataItemMapper.insertDevice(map);
	}
	public void deleteDevice(){
		dataItemMapper.deleteDevice();
	}
	public void deleteDeviceItem(){
		dataItemMapper.deleteDeviceItem();
	}
}
