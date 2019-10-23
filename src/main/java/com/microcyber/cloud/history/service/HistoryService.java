package com.microcyber.cloud.history.service;

import org.springframework.stereotype.Service;

import com.microcyber.cloud.history.model.InfluxQueryParams;
import com.microcyber.cloud.history.model.ReturnResult;

/**
 * @author zhaohongwei
 */
@Service
public interface HistoryService {

	public ReturnResult getQueryList(InfluxQueryParams iqp);//获取查询列表
	public ReturnResult getQueryLast(InfluxQueryParams iqp);//根据，deviceId数据项ID获取最新的值
	public ReturnResult getQueryAllLast(InfluxQueryParams iqp);//根据deviceID获取各个数据项的最新值
	public ReturnResult getGroupQueryList(InfluxQueryParams iqp);//根据deviceID 按照时间分组查询
	public ReturnResult getQueryListByPage(String beginDate,String endDate,String page,String limit,String deviceId,String dataItemId,String dataType,String sort);
	public ReturnResult getGroupQueryByDeviceId(String beginDate,String endDate,String deviceId);



}
