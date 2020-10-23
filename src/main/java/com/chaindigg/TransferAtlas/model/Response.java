package com.chaindigg.TransferAtlas.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Response<T> {

    private Integer code;
    private String msg;
    private T data;

    public Response<T> success(T t){
        this.code = StatusCode.PARSE_SUCCESS.code;
        this.msg = StatusCode.PARSE_SUCCESS.message;
        this.data = t;
        return this;
    }

    public Response<T> fail(StatusCode statusCode){
        this.code = statusCode.code;
        this.msg = statusCode.message;
        this.data = null;
        return this;
    }

}
