package com.microcyber.cloud.history.model;

import java.io.Serializable;

import org.influxdb.annotation.Column;
//数据项表的实体类
public class DataItem implements Serializable {
   
    @Column(name = "data_item_id")
    private Long dataItemId;
    @Column(name = "data_item_name")
    private String dataItemName;
    @Column(name = "data_type")
    private int dataType;
    @Column(name = "persist")
    private int persist;
	public Long getDataItemId() {
		return dataItemId;
	}
	public void setDataItemId(Long dataItemId) {
		this.dataItemId = dataItemId;
	}
	public String getDataItemName() {
		return dataItemName;
	}
	public void setDataItemName(String dataItemName) {
		this.dataItemName = dataItemName;
	}
	public int getDataType() {
		return dataType;
	}

	public int getPersist() {
		return persist;
	}

	public void setPersist(int persist) {
		this.persist = persist;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

}