package com.microcyber.cloud.history.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.microcyber.cloud.history.model.DataItem;

/**
 * 
 */
@Repository
public interface DataItemDAO {
	List<DataItem> getList();
	int batchInsert(List<Map> lm);
	int insertDevice(Map map);
	void deleteDevice();
	void deleteDeviceItem();
}