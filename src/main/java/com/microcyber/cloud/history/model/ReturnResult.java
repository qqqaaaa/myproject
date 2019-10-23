package com.microcyber.cloud.history.model;

import com.alibaba.fastjson.JSON;

/**
 * 统一API响应结果封装
 */

public class ReturnResult {
    private int code;
    private String message = "";
    private Object data;

    public int getCode() {
        return code;
    }

    public ReturnResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ReturnResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ReturnResult setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
