package com.chaindigg.TransferAtlas.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AjaxResponse<T> {

    private int code;   
    private String message;
    private T data;

}