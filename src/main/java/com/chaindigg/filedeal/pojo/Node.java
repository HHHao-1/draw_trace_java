package com.chaindigg.filedeal.pojo;

import java.math.BigDecimal;

public class Node {
    private Integer inCount;
    private Integer outCount;
    private String inValue;
    private String outValue;
    private String value;

    public Integer getInCount() {
        return inCount;
    }

    public void setInCount(Integer inCount) {
        this.inCount = inCount;
    }

    public Integer getOutCount() {
        return outCount;
    }

    public void setOutCount(Integer outCount) {
        this.outCount = outCount;
    }

    public String getInValue() {
        return inValue;
    }

    public void setInValue(String inValue) {
        this.inValue = inValue;
    }

    public String getOutValue() {
        return outValue;
    }

    public void setOutValue(String outValue) {
        this.outValue = outValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
