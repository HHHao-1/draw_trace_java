package com.chaindigg.TransferAtlas.model;

import lombok.Data;

@Data
public class NodeDO {
    private Integer inCount;
    private Integer outCount;
    private String inValue;
    private String outValue;
    private String value;
    private String name;
}
