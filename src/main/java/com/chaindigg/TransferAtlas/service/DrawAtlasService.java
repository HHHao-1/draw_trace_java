package com.chaindigg.TransferAtlas.service;

//import com.chaindigg.TransferAtlas.pojo.Json;

import com.chaindigg.TransferAtlas.model.AjaxResponse;
import com.chaindigg.TransferAtlas.model.LinkDO;
import com.chaindigg.TransferAtlas.model.NodeDO;
import com.chaindigg.TransferAtlas.model.StatusCode;
import com.chaindigg.TransferAtlas.utils.MultipartFileToFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenghao
 */
@Service
public class DrawAtlasService {

    /**
     * 构建绘图信息
     *
     * @param selectFiles
     * @param min
     * @param max
     * @param identification
     * @return Map<String, Object>
     * @throws Exception
     */
    public AjaxResponse dealData(MultipartFile[] selectFiles, String min, String max, String identification) throws IOException {

        Map<String, Object> dealSelectFile = (Map<String, Object>)dealSelectFile(selectFiles).getData();
        Map<String, NodeDO> nodes = (Map<String, NodeDO>) dealSelectFile.get("nodes");
        Map<String, Map<String, String>> links = (Map<String, Map<String, String>>) dealSelectFile.get("links");

        //前端需要的数据结构
        List<NodeDO> nodeList = new ArrayList<>();
        List<LinkDO> linkList = new ArrayList<>();


        Map<String, String> identMap = new HashMap();
        // 设置高亮节点
        if (identification != null && !identification.equals("")) {
            if (identification.contains("，")) {
                return AjaxResponse.fail(StatusCode.S1.code, StatusCode.S1.message);
            } else {
                String[] splitdate = identification.split("\\s+");
                // 解析文件
                for (int i = 0; i < splitdate.length; i++) {
                    // 解析高亮条件
                    String[] data = splitdate[i].split(",");
                    String address = data[0];
                    String ident = data[1];
                    identMap.put(address, ident);
                }
            }
        }

        // 设置最大值和最小值
        BigDecimal minValue = new BigDecimal(-1);
        BigDecimal maxValue = new BigDecimal(-1);
        if (!min.equals("")) {
            minValue = new BigDecimal(min);
        }
        if (!max.equals("")) {
            maxValue = new BigDecimal(max);
        }

        boolean filtered = false;

        Map<String, NodeDO> tmpNodeMap = new HashMap();
        // 构建绘制需要的node节点信息，构建nodelist
        for (Map.Entry<String, NodeDO> entry : nodes.entrySet()) {
            if (identMap.get(entry.getKey()) == "-") {
                continue;
            }
            NodeDO node = new NodeDO();
            node.setName(entry.getKey());
            node.setValue(entry.getValue().getValue());
            node.setInCount(entry.getValue().getInCount());
            node.setOutCount(entry.getValue().getOutCount());
            node.setInValue(entry.getValue().getInValue());
            node.setOutValue(entry.getValue().getOutValue());
            nodeList.add(node);
            tmpNodeMap.put(entry.getKey(), node);
        }

        // 构建绘制需要的link信息，构建linklist
        for (Map.Entry<String, Map<String, String>> entry1 : links.entrySet()) {
            // from节点
            LinkDO link = new LinkDO();
            link.setSource(tmpNodeMap.get(entry1.getKey()));
            // to节点
            for (Map.Entry<String, String> entry2 : entry1.getValue().entrySet()) {
                if (identMap.get(entry2.getKey()) == "-") {
                    continue;
                }
                LinkDO link2 = new LinkDO();
                link2.setSource(link.getSource());
                link2.setTarget(tmpNodeMap.get(entry2.getKey()));
                // 用于样式
                link2.setType("resolved");
                link2.setValue(String.valueOf(entry2.getValue()));

                // 过滤不符合条件的link
                if (minValue.compareTo(new BigDecimal(-1)) != 0 && new BigDecimal(entry2.getValue()).compareTo(minValue) == -1) {
                    filtered = true;
                    continue;
                }
                if (maxValue.compareTo(new BigDecimal(-1)) != 0 && new BigDecimal(entry2.getValue()).compareTo(maxValue) == 1) {
                    filtered = true;
                    continue;
                }

                linkList.add(link2);
            }
        }

        // 根据过滤结果，去掉node
        if (filtered) {
            for (int i = nodeList.size() - 1; i >= 0; i--) {
                boolean find = false;
                for (int j = 0; j < linkList.size(); j++) {
                    if (nodeList.get(i).getName() == linkList.get(j).getSource().getName() ||
                            nodeList.get(i).getName() == linkList.get(j).getTarget().getName()) {
                        find = true;
                        break;
                    }
                }

                if (!find) {
                    nodeList.remove(i);
                }
            }
        }

        //构建返回值
        HashMap<String, Object> response = new HashMap<String, Object>() {
            {
                put("nodeList", nodeList);
                put("linkList", linkList);
                put("identMap", identMap);
            }
        };
        return AjaxResponse.success(response);
    }

    /***
     * 处理csv文件
     * @param selectFiles
     * @return
     * @throws Exception
     */
    public AjaxResponse dealSelectFile(MultipartFile[] selectFiles) throws IOException {

        Map<String, NodeDO> nodes = new HashMap<>();
        Map<String, Map<String, String>> links = new HashMap<>();
        Map<String, String> txs = new HashMap();


        if (selectFiles.length > 0) {
            for (MultipartFile selectFile : selectFiles) {
                File inFile = MultipartFileToFile.multipartFileToFile(selectFile);
                try (BufferedReader reader = Files.newBufferedReader(inFile.toPath())) {
                    String inString;
                    int i = 0;
                    while ((inString = reader.readLine()) != null) {
                        if (i == 0) {
                            i++;
                            continue;
                        }
                        // 解析csv文件数据
                        String[] data = inString.split(",");
                        String tx = data[0];
                        String from = data[1];
                        String to = data[2];
                        BigDecimal value = new BigDecimal(data[3]);

                        if (tx.length() == 0) {
                            break;
                        }

                        if (txs.containsValue(tx)) {
                            continue;
                        }
                        txs.put(tx, "");

                        // 将value累加到node节点上，from为负数，to为正数
                        if (nodes.containsKey(from)) {
                            NodeDO node = nodes.get(from);
                            Integer outCount = node.getOutCount();
                            node.setOutCount(++outCount);
                            node.setOutValue(String.valueOf(new BigDecimal(node.getOutValue()).add(value)));
                            node.setValue(String.valueOf(new BigDecimal(node.getValue()).add(value.multiply(new BigDecimal(-1)))));
                            nodes.put(from, node);
                        } else {
                            NodeDO node1 = new NodeDO();
                            node1.setOutCount(1);
                            node1.setInCount(0);
                            node1.setOutValue((new BigDecimal(String.valueOf(value))).toString());
                            node1.setInValue(String.valueOf(new BigDecimal(0)));
                            node1.setValue(String.valueOf(value.multiply(new BigDecimal(-1))));
                            nodes.put(from, node1);
                        }

                        if (nodes.containsKey(to)) {
                            NodeDO node = nodes.get(to);
                            Integer inCount = node.getInCount();
                            node.setInCount(++inCount);
                            node.setInValue(String.valueOf(new BigDecimal(node.getInValue()).add(value)));
                            node.setValue(String.valueOf(new BigDecimal(node.getValue()).add(value)));
                            nodes.put(to, node);
                        } else {
                            NodeDO node2 = new NodeDO();
                            node2.setOutCount(0);
                            node2.setInCount(1);
                            node2.setOutValue(String.valueOf(new BigDecimal(0)));
                            node2.setInValue(String.valueOf(value));
                            node2.setValue(String.valueOf(value));
                            nodes.put(to, node2);
                        }
                        Map<String, String> link = new HashMap<>();
                        // 计算节点关系，形式为Map<from, Map<to, value>>，value累加
                        if (links.containsKey(from)) {
                            link = links.get(from);
                            if (link.containsKey(to)) {
                                link.put(to, String.valueOf(new BigDecimal(link.get(to)).add(value)));
                            } else {
                                link.put(to, String.valueOf(value));
                            }
                        } else {
                            link.put(to, String.valueOf(value));
                            links.put(from, link);
                        }
                    }
                }
                MultipartFileToFile.delteTempFile(inFile);
            }
        }else{
            return AjaxResponse.fail(StatusCode.S2.code, StatusCode.S2.message);
        }
        Map<String, Object> response = new HashMap<String, Object>() {
            {
                put("nodes", nodes);
                put("links", links);
            }
        };
        return AjaxResponse.success(response);
    }
}