package com.microcyber.cloud.history.model;

/**定义历史服务查询接口的参数
 * @author zhaohongwei
 *
 */
public class InfluxQueryParams {

	private String deviceId;//设备号,查询就需要
	private String dataItemId;//数据项id
	private String dataType;//数据项id
	private String dataItemName;//数据项名字
	private String startTime;//开始时间
	private String endTime;//结束时间
	private boolean sort = true;//排序规则，默认降序
	private boolean group = false;//是否分组查询
	private int aggMethod;//聚合函数的方法
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDataItemId() {
		return dataItemId;
	}
	public void setDataItemId(String dataItemId) {
		this.dataItemId = dataItemId;
	}
	public String getDataItemName() {
		return dataItemName;
	}
	public void setDataItemName(String dataItemName) {
		this.dataItemName = dataItemName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean isSort() {
		return sort;
	}
	public void setSort(boolean sort) {
		this.sort = sort;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
