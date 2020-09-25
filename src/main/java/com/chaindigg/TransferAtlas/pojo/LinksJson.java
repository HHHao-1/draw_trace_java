package com.chaindigg.TransferAtlas.pojo;

public class LinksJson {
    private Json source;
    private Json target;
    private String type;
    private String value;

    public Json getSource() {
        return source;
    }

    public void setSource(Json source) {
        this.source = source;
    }

    public Json getTarget() {
        return target;
    }

    public void setTarget(Json target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
