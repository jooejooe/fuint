package com.fuint.application.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求结果
 *
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ReqResult implements Serializable {

    private String resultCode;
    private String msg;
    private boolean result;
    private Map<String, Object> data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    public Map<String, Object> getData() {
        return this.data;
    }
}
