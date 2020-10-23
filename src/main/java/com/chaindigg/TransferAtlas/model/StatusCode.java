package com.chaindigg.TransferAtlas.model;

public enum StatusCode {
    //状态码
    PARSE_SUCCESS(1000,"解析成功"),
    PARAMS_ERROR(1001,"输入参数错误"),
    FILE_ERROR(1002,"文件导入失败"),
    PARSE_ERROR(1003,"解析失败");

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int code;

    public String message;
}
