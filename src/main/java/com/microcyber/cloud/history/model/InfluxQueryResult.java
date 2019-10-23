package com.microcyber.cloud.history.model;

/**定义历史服务查询接口的返回数据
 * @author zhaohongwei
 *
 */
public class InfluxQueryResult {
	private String time;
	private int intValue;
	private Double floatValue;
	private String strValue;
	private boolean boolValue;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public Double getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public boolean isBoolValue() {
		return boolValue;
	}
	public void setBoolValue(boolean boolValue) {
		this.boolValue = boolValue;
	}
	

	
}
