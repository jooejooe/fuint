package com.fuint.application.enums;

/**
 * 分组类型
 * <p/>
 * Created by zach on 2021/1/19.
 */
public enum GroupTypeEnum {
    COUPON("C", "卡券"),
    PRESTORE("P", "预存卡"),
    TIMER("T", "集次卡");

    private String key;

    private String value;

    GroupTypeEnum(String key, String value) {
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
