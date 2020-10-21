package com.chaindigg.TransferAtlas.model;

import lombok.Data;

@Data
public class LinkDO {
    private NodeDO source;
    private NodeDO target;
    private String type;
    private String value;
}
