package com.chaindigg.TransferAtlas.model;

public enum StatusCode {
    //状态码
    S0(1000,"success"),
    S1(1001,"输入格式错误"),
    S2(1002,"未导入文件"),
    S3(1003,"解析失败");

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int code;

    public String message;
}
