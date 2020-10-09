package com.chaindigg.TransferAtlas.model;

import lombok.Data;

@Data
public class AjaxResponse {

    private int code;   
    private String message;
    private Object data;


    public static AjaxResponse success(Object data) {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setCode(StatusCode.S0.code);
        resultBean.setMessage(StatusCode.S0.message);
        resultBean.setData(data);
        return resultBean;
    }

    public static AjaxResponse fail(int code, String message) {
        AjaxResponse resultBean = new AjaxResponse();
        resultBean.setCode(code);
        resultBean.setMessage(message);
        return resultBean;
    }


}