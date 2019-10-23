package com.microcyber.cloud.history.model;

import java.util.Map;
//定义kafka数据的转换模型
public class KafkaModel {

    private String gatewayId;
    private String deviceId;
    private String time;
    private Map<String,Object> dataItems;

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String,Object> getDataItems() {
        return dataItems;
    }

    public void setDataItems(Map<String,Object> dataItems) {
        this.dataItems = dataItems;
    }
}
