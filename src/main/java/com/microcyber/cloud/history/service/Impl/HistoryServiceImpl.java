package com.microcyber.cloud.history.service.Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.microcyber.cloud.history.influxdb.InfluxDBClient;
import com.microcyber.cloud.history.model.InfluxQueryParams;
import com.microcyber.cloud.history.model.InfluxQueryResult;
import com.microcyber.cloud.history.model.ReturnResult;
import com.microcyber.cloud.history.service.HistoryService;
import com.microcyber.cloud.history.task.DataItemScheduleTask;
import com.microcyber.cloud.history.tool.TimeTurnTools;

/**
 * @author zhaohongwei
 *
 */
@Service
public class HistoryServiceImpl implements HistoryService {
	@Autowired
	private InfluxDBClient influxDB;

	@Override
	public ReturnResult getQueryList(InfluxQueryParams iqp) {
		ReturnResult returnResult = new ReturnResult();
		Map selectResult = new HashMap();
		String deviceId = iqp.getDeviceId();
		if(!(deviceId!=null && !"".equals(deviceId))){
			returnResult.setMessage("deviceId为空");
			return returnResult;
		}
		selectResult.put("deviceId",deviceId);
		String dataType = iqp.getDataType();
		String sql = "select type,time,int_value,float_value,str_value,bool_value from \"" + deviceId + "\" where 1=1";
		try {
			if (iqp.getDataItemId() != null && !"".equals(iqp.getDataItemId())) {
				selectResult.put("id",iqp.getDataItemId());
				sql = sql + " and ID = '" + iqp.getDataItemId() + "'";
			} else {
				returnResult.setMessage("DataItemId为空");
				return returnResult;
			}
			if (dataType != null && !"".equals(dataType)) {
				selectResult.put("type",dataType);
				sql = sql + " and type = '" + dataType + "'";
			}else{
				returnResult.setMessage("type为空");
				return returnResult;
			}
			if (iqp.getStartTime() != null && !"".equals(iqp.getStartTime())) {
				sql = sql + " and time >= '" + pastDays(iqp.getStartTime()) + "'";
			} else {
				returnResult.setMessage("开始时间为空");
				return returnResult;
			}
			if (iqp.getEndTime() != null && !"".equals(iqp.getEndTime())) {
				sql = sql + " and time <= '" + pastDays(iqp.getEndTime()) + "'";
			} else {
				returnResult.setMessage("结束时间为空");
				return returnResult;
			}
			if (differentDaysByMillisecond(iqp.getStartTime(), iqp.getEndTime()) > 7) {
				returnResult.setMessage("查询时间间隔大于7天");
				return returnResult;
			}
		} catch (ParseException e1) {
			returnResult.setMessage(e1.toString());
			return returnResult;
		}
		String sort = "desc";
		if (!iqp.isSort()) {
			sort = "asc";
		}
		sql = sql + " order by time " + sort;
		List<Map> lists = new ArrayList<Map>();
		try {
			QueryResult results = influxDB.query(sql);
			for (Result result : results.getResults()) {
				List<Series> series = result.getSeries();
				if (series != null) {
					for (Series serie : series) {
						List<List<Object>> values = serie.getValues();
						List<String> columns = serie.getColumns();
						lists.addAll(getQueryData(columns, values, dataType));
					}
				}
			}
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		selectResult.put("data",lists);
		returnResult.setData(selectResult);
		return returnResult;
	}

	@Override
	public ReturnResult getQueryLast(InfluxQueryParams iqp) {
		ReturnResult returnResult = new ReturnResult();
		String dataType = iqp.getDataType();
		String f = "";
		Map res = new HashMap();
		try {
			if ("1".equals(dataType)) {
				f = "int_value";
			} else if ("2".equals(dataType)) {
				f = "float_value";
			} else if ("3".equals(dataType)) {
				f = "str_value";
			} else if ("4".equals(dataType)) {
				f = "bool_value";
			}
			String sql = "select last(" + f + "),time from \"" + iqp.getDeviceId() + "\" where 1=1";
			if (iqp.getDataItemId() != null && !"".equals(iqp.getDataItemId())) {
				sql = sql + " and ID = '" + iqp.getDataItemId() + "'";
			}
			if (iqp.getDataType() != null && !"".equals(iqp.getDataType())) {
				sql = sql + " and type = '" + iqp.getDataType() + "'";
			}
			if (iqp.getStartTime() != null && !"".equals(iqp.getStartTime())) {
				sql = sql + " and time >= '" + pastDays(iqp.getStartTime()) + "'";
			}
			if (iqp.getEndTime() != null && !"".equals(iqp.getEndTime())) {
				sql = sql + " and time <= '" + pastDays(iqp.getEndTime()) + "'";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Object values = "";
			long time = 0;
			QueryResult results = influxDB.query(sql);
			if (results.getResults().size() > 0) {
				Result result = results.getResults().get(0);
				if (result != null) {
					List<Series> series = result.getSeries();
					if (series != null && series.size() > 0) {
						List<List<Object>> value = series.get(0).getValues();
						if (value != null && value.size() > 0) {
							values = String.valueOf(value.get(0).get(1));
							Date after = TimeTurnTools.worldToChina(String.valueOf(value.get(0).get(0)));
							res.put("values", values);
							res.put("time", after.getTime());
						}
					}
				}
			}
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		returnResult.setData(res);
		return returnResult;
	}

	@Override
	public ReturnResult getQueryAllLast(InfluxQueryParams iqp) {
		Map data_types = DataItemScheduleTask.data_types;
		//JSONObject json_data_types = JSONObject.parseObject(data_types);
		ReturnResult returnResult = new ReturnResult();
		List<Map> rdata = new ArrayList<Map>();
		try {
			String sql = "select last(int_value) as f1,last(float_value) as f2,last(str_value) as f3,last(bool_value) as f4 from \""
					+ iqp.getDeviceId() + "\" where 1=1";
			if (iqp.getStartTime() != null && !"".equals(iqp.getStartTime())) {
				sql = sql + " and time >= '" + pastDays(iqp.getStartTime()) + "'";
			}
			if (iqp.getEndTime() != null && !"".equals(iqp.getEndTime())) {
				sql = sql + " and time <= '" + pastDays(iqp.getEndTime()) + "'";
			}
			sql = sql + " group by ID";
			System.out.println(sql);
			Object values = "";
			QueryResult results = influxDB.query(sql);
			if (results.getResults().size() > 0) {
				Result result = results.getResults().get(0);
				if (result != null) {
					List<Series> series = result.getSeries();
					for (int i = 0; i < series.size(); i++) {
						Map aMap = new HashMap();
						List<List<Object>> value = series.get(i).getValues();
						Map valueTags = series.get(i).getTags();
						String id = valueTags.get("ID").toString();
						if(data_types.containsKey(id)){
							String data_type = data_types.get(id).toString();
							if (value.size() > 0) {
								String f1 = String.valueOf(value.get(0).get(1));
								String f2 = String.valueOf(value.get(0).get(2));
								String f3 = String.valueOf(value.get(0).get(3));
								String f4 = String.valueOf(value.get(0).get(4));
								if ("1".equals(data_type)) {
									aMap.put("newest", f1);
								} else if ("2".equals(data_type)) {
									aMap.put("newest", f2);
								} else if ("3".equals(data_type)) {
									aMap.put("newest", f3);
								} else if ("4".equals(data_type)) {
									aMap.put("newest", f4);
								}
								aMap.put("ID", id);
								rdata.add(aMap);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		returnResult.setData(rdata);
		return returnResult;
	}
	/**
	 *按照时间12m 分组查询float的和
	 */
	@Override
	public ReturnResult getGroupQueryList(InfluxQueryParams iqp) {
		ReturnResult returnResult = new ReturnResult();
		List<Map> lm = new ArrayList<Map>();
		String dataType = iqp.getDataType();
		String sql = "select sum(float_value) from \"" + iqp.getDeviceId() + "\" where 1=1";
		try {
			if (iqp.getDataItemId() != null && !"".equals(iqp.getDataItemId())) {
				sql = sql + " and ID = '" + iqp.getDataItemId() + "'";
			} else {
				returnResult.setMessage("DataItemId为空");
				return returnResult;
			}
			if (iqp.getDataType() != null && !"".equals(iqp.getDataType())) {
				sql = sql + " and type = '" + iqp.getDataType() + "'";
			}
			if (iqp.getStartTime() != null && !"".equals(iqp.getStartTime())) {
				sql = sql + " and time >= '" + pastDays(iqp.getStartTime()) + "'";
			} else {
				returnResult.setMessage("开始时间为空");
				return returnResult;
			}
			if (iqp.getEndTime() != null && !"".equals(iqp.getEndTime())) {
				sql = sql + " and time <= '" + pastDays(iqp.getEndTime()) + "'";
			} else {
				returnResult.setMessage("结束时间为空");
				return returnResult;
			}
			if (differentDaysByMillisecond(iqp.getStartTime(), iqp.getEndTime()) > 7) {
				returnResult.setMessage("查询时间间隔大于7天");
				return returnResult;
			}
		} catch (ParseException e1) {
			returnResult.setMessage(e1.toString());
			return returnResult;
		}
		String sort = "desc";
		sql = sql + " group by time(1h)";
		try {
			QueryResult results = influxDB.query(sql);
			for (Result result : results.getResults()) {
				List<Series> series = result.getSeries();
				if (series != null) {
					for (Series serie : series) {
						List<List<Object>> values = serie.getValues();
						for(List<Object> value :values) {
							Map sumMap = new HashMap();
							sumMap.put("time", TimeTurnTools.worldToChina(value.get(0).toString()).getTime());
							sumMap.put("sumValue",value.get(1));
							lm.add(sumMap);
						}
					}
				}
			}
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		returnResult.setData(lm);
		return returnResult;
	}
	/**
	 * 根据分页查询数据
	 */
	@Override
	public ReturnResult getQueryListByPage(String beginDate, String endDate, String page, String limit, String deviceId, String dataItemId,String dataType,String sort) {
		ReturnResult returnResult = new ReturnResult();
		Map ret = new HashMap();
		String sql = "";
		if (deviceId != null && !"".equals(deviceId)) {
			sql = "select time,int_value,float_value,str_value,bool_value from \"" + deviceId + "\" where 1=1";
		} else {
			returnResult.setMessage("deviceId为空");
			return returnResult;
		}
		try {
			if (dataItemId != null && !"".equals(dataItemId)) {
				sql = sql + " and ID = '" + dataItemId + "'";
			} else {
				returnResult.setMessage("DataItemId为空");
				return returnResult;
			}
			if (dataType != null && !"".equals(dataType)) {
				sql = sql + " and type = '" + dataType + "'";
			}else {
				returnResult.setMessage("type为空");
				return returnResult;
			}
			if (beginDate != null && !"".equals(beginDate)) {
				sql = sql + " and time >= '" + pastDays(beginDate) + "'";
			} else {
				returnResult.setMessage("开始时间为空");
				return returnResult;
			}
			if (endDate != null && !"".equals(endDate)) {
				sql = sql + " and time <= '" + pastDays(endDate) + "'";
			} else {
				returnResult.setMessage("结束时间为空");
				return returnResult;
			}
		} catch (ParseException e1) {
			returnResult.setMessage(e1.toString());
			return returnResult;
		}
		if("0".equals(page)){  //导出时page入参为0
			sql = sql + " order by time " + sort;
		}else{
			int offset = (Integer.parseInt(page)-1)*Integer.parseInt(limit);
			sql = sql + " order by time " + sort + " limit "+limit +" offset "+offset;
		}
		System.out.println(sql);
		List<Map> lists = new ArrayList<Map>();
		try {
			QueryResult results = influxDB.query(sql);
			for (Result result : results.getResults()) {
				List<Series> series = result.getSeries();
				if (series != null) {
					for (Series serie : series) {
						List<List<Object>> values = serie.getValues();
						List<String> columns = serie.getColumns();
						lists.addAll(getQueryData(columns, values, dataType));
					}
				}
			}
			int total = getQueryListCount(beginDate,endDate,deviceId,dataItemId,dataType);
			ret.put("total",total);
			ret.put("list",lists);
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		returnResult.setData(ret);
		return returnResult;
	}
	/**
	 * 查询数据总条数
	 */
	private int getQueryListCount(String beginDate, String endDate, String deviceId, String dataItemId, String dataType) throws Exception {
		int total = 0;
		String csql = "count(*)";
		if (dataType != null && !"".equals(dataType)) {
			if("1".equals(dataType)){
				csql = "count(int_value)";
			}else if ("2".equals(dataType)){
				csql = "count(float_value)";
			}else if ("3".equals(dataType)){
				csql = "count(str_value)";
			}else if ("4".equals(dataType)){
				csql = "count(bool_value)";
			}
		}
		String sql = "select "+csql+" from  \"" + deviceId + "\" where 1=1";
		if (dataItemId != null && !"".equals(dataItemId)) {
			sql = sql + " and ID = '" + dataItemId + "'";
		}
		if (dataType != null && !"".equals(dataType)) {
			sql = sql + " and type = '" + dataType + "'";
		}
		if (beginDate != null && !"".equals(beginDate)) {
			sql = sql + " and time >= '" + pastDays(beginDate) + "'";
		}
		if (endDate != null && !"".equals(endDate)) {
			sql = sql + " and time <= '" + pastDays(endDate) + "'";
		}
		System.out.println(sql);

		List<InfluxQueryResult> lists = new ArrayList<InfluxQueryResult>();
			QueryResult results = influxDB.query(sql);
			for (Result result : results.getResults()) {
				List<Series> series = result.getSeries();
				if (series != null) {
					for (Series serie : series) {
						List<List<Object>> values = serie.getValues();
						if(values.size()>0) {
							if(values.get(0).size()>1) {
								Double t = (Double) values.get(0).get(1);
								if(t!=null) {
									total = t.intValue();
								}
							}
						}
					}
				}
			}
		return total;
	}
	/**
	 *按照时间12m 分组查询float的和
	 */
	public ReturnResult getGroupQueryByDeviceId(String beginDate, String endDate, String deviceId) {
		ReturnResult returnResult = new ReturnResult();
		List<Map> lm = new ArrayList<Map>();
		String sql = "select count(*) as count from \"" + deviceId + "\" where 1=1";
		try {
			if (beginDate != null && !"".equals(beginDate)) {
				sql = sql + " and time >= '" + pastDays(beginDate) + "'";
			} else {
				returnResult.setMessage("开始时间为空");
				return returnResult;
			}
			if (endDate != null && !"".equals(endDate)) {
				sql = sql + " and time <= '" + pastDays(endDate) + "'";
			} else {
				returnResult.setMessage("结束时间为空");
				return returnResult;
			}
		} catch (ParseException e1) {
			returnResult.setMessage(e1.toString());
			return returnResult;
		}
		String sort = "desc";
		sql = sql + " group by time(1d)";
		List<Map> l=new ArrayList<Map>();
		int total = 0;
		try {
			QueryResult results = influxDB.query(sql);
			for (Result result : results.getResults()) {
				List<Series> series = result.getSeries();
				if (series != null) {
					for (Series serie : series) {
						List<List<Object>> values = serie.getValues();
						for(List<Object> value :values) {
							int size = value.size();
							Map sumMap = new HashMap();
							sumMap.put("time", TimeTurnTools.worldToChina(value.get(0).toString()).getTime());
							for(int i = 1;i<size;i++){
								if((Double)value.get(i)>0){
									total++;
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			returnResult.setMessage(e.toString());
			returnResult.setCode(500);
		}
		//System.out.println(sql);
		returnResult.setData(total);
		return returnResult;
	}
	private List<Map> getQueryData(List<String> columns, List<List<Object>> values, String dataType)
			throws BeansException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		//List<InfluxQueryResult> lists = new ArrayList<InfluxQueryResult>();
		List<Map> lm = new ArrayList<Map>();
		for (List<Object> list : values) {
//			InfluxQueryResult info = new InfluxQueryResult();
//			BeanWrapperImpl bean = new BeanWrapperImpl(info);
			Map singleMap = new HashMap();
			for (int i = 0; i < list.size(); i++) {
				String propertyName = setColumns(columns.get(i));// 字段名
				Object value = list.get(i);// 相应字段值
				if ("time".equals(propertyName)) {
					df.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date after = df.parse(value.toString());
					//bean.setPropertyValue(propertyName, sdf.format(after));
					singleMap.put("time",sdf.format(after));
				}
				if("1".equals(dataType)){
					if ("intValue".equals(propertyName)) {
						singleMap.put("dvalue",value == null ? "" : value);
					}
				}else if("2".equals(dataType)){
					if ("floatValue".equals(propertyName)) {
						singleMap.put("dvalue",value == null ? "" : value);
					}
				}else if("3".equals(dataType)){
					if ("strValue".equals(propertyName)) {
						singleMap.put("dvalue",value == null ? "" : value);
					}
				}else if("4".equals(dataType)){
					if ("boolValue".equals(propertyName)) {
						singleMap.put("dvalue",value == null ? "" : value);
					}
				}
			}
			lm.add(singleMap);
//			for (int i = 0; i < list.size(); i++) {
//				String propertyName = setColumns(columns.get(i));// 字段名
//				Object value = list.get(i);// 相应字段值
//				if ("time".equals(propertyName)) {
//					df.setTimeZone(TimeZone.getTimeZone("UTC"));
//					Date after = df.parse(value.toString());
//					bean.setPropertyValue(propertyName, sdf.format(after));
//				} else {
//					if (value != null) {
//						bean.setPropertyValue(propertyName, value == null ? "" : value);
//					}
//				}
//			}
			//lists.add(info);
		}
		return lm;
	}

	/*** 转义字段 ***/
	private String setColumns(String column) {
		String[] cols = column.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cols.length; i++) {
			String col = cols[i].toLowerCase();
			if (i != 0) {
				String start = col.substring(0, 1).toUpperCase();
				String end = col.substring(1).toLowerCase();
				col = start + end;
			}
			sb.append(col);
		}
		return sb.toString();
	}

	/**
	 * 通过时间秒毫秒数判断两个时间的间隔
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static int differentDaysByMillisecond(String startTime, String endTime) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = sdf.parse(startTime);
		Date date2 = sdf.parse(endTime);
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	/**
	 * @param sourceDate 原始时间
	 * @return 八小时之前的时间
	 * @throws ParseException
	 */
	public static String pastDays(String sourceDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endTime = "";
		long currentTime = sdf.parse(sourceDate).getTime() - 8 * 60 * 60 * 1000;
		Date date = new Date(currentTime);
		String nowTime = "";
		nowTime = sdf.format(date);
		return nowTime;
	}
}
