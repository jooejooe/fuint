package com.fuint.application.enums;

/**
 * 售后订单状态枚举
 * Created by FSQ
 * Contact wx fsq_better
 */
public enum RefundStatusEnum {

    CREATED("A", "待审核"),
    APPROVED("B", "已同意"),
    REJECT("C", "已拒绝");

    private String key;

    private String value;

    RefundStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
