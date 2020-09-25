package com.chaindigg.filedeal.pojo;

import java.math.BigDecimal;
import java.util.Map;

public class Response {
    private Map<String, Node> nodes;
    private Map<String, Map<String, BigDecimal>> links;
    private Map<String, String> txs;

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    public Map<String, Map<String, BigDecimal>> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Map<String, BigDecimal>> links) {
        this.links = links;
    }

    public Map<String, String> getTxs() {
        return txs;
    }

    public void setTxs(Map<String, String> txs) {
        this.txs = txs;
    }

    @Override
    public String toString() {
        return "Response{" +
                "nodes=" + nodes +
                ", links=" + links +
                ", txs=" + txs +
                '}';
    }
}
