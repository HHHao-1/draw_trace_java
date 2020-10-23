package com.chaindigg.TransferAtlas.service;

import com.chaindigg.TransferAtlas.model.Response;
import com.chaindigg.TransferAtlas.model.StatusCode;
import com.chaindigg.TransferAtlas.utils.MultipartFileToFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class HuobiStatService {

//    @Resource
//    private Response response;


    public List<ArrayList<Object>> dataList1 = new ArrayList<>();//oct交易记录
    public List<ArrayList<Object>> dataList2 = new ArrayList<>();//充提记录
    public List<ArrayList<Object>> dataList3 = new ArrayList<>();//虚拟币交易记录
    public List<ArrayList<String>> userList = new ArrayList<>();//用户名集合
    public List sum = new ArrayList();//输送到前台的数据

    //读取文件
    public Workbook readWorkbookFromLocalFile(MultipartFile selectFile) throws Exception {
        File inFile = MultipartFileToFile.multipartFileToFile(selectFile);
        FileInputStream fis = new FileInputStream(inFile);
        Workbook workbook = new XSSFWorkbook(fis);
        return workbook;
    }



    //输出表格
    public Response outputWorkbook(Workbook workbook){
        Response response = new Response();
        //遍历所有sheet，数据预处理
        for (Sheet sheet:workbook) {
            if(sheet.getSheetName().equals("otc交易记录")){
                for (Row row:sheet) {
                    ArrayList<Object> data = new ArrayList<Object>();
                    if(row.getRowNum() == 0){
                        continue;
                    }
                    for (Cell cell:row) {
                        Integer columnNum = cell.getColumnIndex();
                        if(columnNum == 0 || columnNum == 1 || columnNum == 4){
                            data.add(new BigDecimal(cell.toString()));
                        }else if(columnNum == 5){
                            //转换为统一日期格式
                            Date date0 = new Date(cell.toString().replace('-','/'));
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String date = ft.format(date0);
                            data.add(date);
                        }else if(columnNum == 3){
                            data.add(cell.toString().toUpperCase());
                        }else if(columnNum == 7 || columnNum == 8){
                            cell.setCellType(CellType.STRING);
                            data.add(cell.toString());
                        }else {
                            data.add(cell.toString());
                        }
                        columnNum++;

                        if(columnNum == 10){
                            dataList1.add(row.getRowNum()-1, data);
                        }
                    }
                }

            }else if (sheet.getSheetName().equals("充提记录")){
                for (Row row:sheet) {
                    ArrayList<Object> data = new ArrayList<Object>();
                    if(row.getRowNum() == 0){
                        continue;
                    }
                    for (Cell cell:row) {
                        Integer columnNum = cell.getColumnIndex();
                        if (columnNum == 5) {
                            data.add(new BigDecimal(cell.toString()));
                        }else if(columnNum == 2){
                            cell.setCellType(CellType.STRING);
                            data.add(cell.toString());
                        }else if(columnNum == 6){
                            Date date0 = new Date(cell.toString().replace('-','/'));
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String date = ft.format(date0);
                            data.add(date);
                        }else if(columnNum == 4){
                            data.add(cell.toString().toUpperCase());
                        }else {
                            data.add(cell.toString());
                        }
                        columnNum++;

                        if(columnNum == 7){
                            dataList2.add(row.getRowNum()-1, data);
                        }
                    }
                }

            }else if(sheet.getSheetName().equals("虚拟币交易记录")){
                for (Row row:sheet) {
                    ArrayList<Object> data = new ArrayList<>();
                    if(row.getRowNum() == 0){
                        continue;
                    }
                    for (Cell cell:row) {
                        Integer columnNum = cell.getColumnIndex();
                        if(columnNum == 0 || columnNum == 1 || columnNum == 2){
                            data.add(new BigDecimal(cell.toString()));
                        }else if(columnNum == 4 || columnNum == 8){
                            cell.setCellType(CellType.STRING);
                            data.add(cell.toString());
                        }else if(columnNum == 7){
                            Date date0 = new Date(cell.toString().replace('-','/'));
                            date0.setTime(date0.getTime()+(8*60)*60*1000);
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String date = ft.format(date0);
                            data.add(date);
                        }else if(columnNum == 6){
                            data.add(cell.toString().toUpperCase());
                        }else {
                            data.add(cell.toString());
                        }
                        columnNum++;

                        if(columnNum == 9){
                            dataList3.add(row.getRowNum()-1, data);
                        }
                    }
                }

            }else if(sheet.getSheetName().equals("用户注册信息")){
                for (Row row:sheet) {
                    ArrayList<String> user = new ArrayList<>();
                    if(row.getRowNum() == 0){
                        continue;
                    }
                    for (Cell cell:row) {
                        Integer columnNum = cell.getColumnIndex();
                        if(columnNum == 5 || columnNum == 7){
                            user.add((cell.toString()));
                        }
                        columnNum++;

                        if(columnNum == 11){
                            userList.add(row.getRowNum()-1, user);
                        }
                    }
                }
            }
        }

        //生成所有用户列表
        for (ArrayList userId:userList) {
            sum.add(generateWorksheetsSum(userId));
            sum.add(generateWorksheetsDetails(userId));
        }

        if(dataList1.isEmpty()){
            return response.fail(StatusCode.PARAMS_ERROR);
        }else if(dataList2.isEmpty()){
            return response.fail(StatusCode.PARAMS_ERROR);
        }else if(dataList3.isEmpty()){
            return response.fail(StatusCode.PARAMS_ERROR);
        }else if(userList.isEmpty()){
            return response.fail(StatusCode.PARAMS_ERROR);
        }else {
            return response.success(sum);
        }

    }

    //生成汇总表格
    public List<ArrayList<Object>> generateWorksheetsSum(ArrayList<String> user){

        List<ArrayList<Object>> results = new ArrayList<>();
        //将多张表格数据整合到一张中
        for (ArrayList i:dataList1) {
            ArrayList<Object> result = new ArrayList<>();
            if(!i.get(8).equals(user.get(0))){
                continue;
            }
            result.add(i.get(5));
            result.add(i.get(7));
            if(i.get(9).equals("买")){
                result.add("法币买入");
                result.add(i.get(6));
                result.add(((BigDecimal)i.get(0)).multiply(new BigDecimal(-1)));
                result.add(i.get(3));
                result.add(i.get(4));
            }else {
                result.add("法币卖出");
                result.add(i.get(3));
                result.add(((BigDecimal)i.get(4)).multiply(new BigDecimal(-1)));
                result.add(i.get(6));
                result.add(i.get(0));
            }
            result.add("");
            result.add("-");
            results.add(result);
        }

        for (ArrayList i:dataList2) {
            ArrayList<Object> result = new ArrayList<>();
            if(!i.get(2).equals(user.get(0))){
                continue;
            }
            result.add(i.get(6));
            result.add(i.get(0));
            result.add(i.get(3));
            if(i.get(3).equals("充币")){
                result.add("-");
                result.add("-");
                result.add(i.get(4));
                result.add(i.get(5));
            }else {
                result.add(i.get(4));
                result.add(((BigDecimal)i.get(5)).multiply(new BigDecimal(-1)));
                result.add("-");
                result.add("-");
            }
            result.add("");
            result.add(i.get(1));
            results.add(result);
        }

            for (ArrayList i:dataList3) {
            ArrayList<Object> result = new ArrayList<>();
            if(!i.get(4).equals(user.get(0))){
                continue;
            }
            result.add(i.get(7));
            result.add(i.get(8));
            result.add("币币兑换");
            //分割币种
            String first = i.get(6).toString().split("/")[0];
            String second = i.get(6).toString().split("/")[1];
            if(i.get(5).equals("买")){
                result.add(second);
                result.add(((BigDecimal)i.get(1)).multiply(new BigDecimal(-1)));
                result.add(first);
                result.add(i.get(2));
            }else {
                result.add(first);
                result.add(((BigDecimal)i.get(2)).multiply(new BigDecimal(-1)));
                result.add(second);
                result.add(i.get(1));
            }
            result.add("");
            result.add("-");
            results.add(result);
        }

        //按日期排序
        sort(results,0);

        Map<String, BigDecimal> values = new LinkedHashMap<>();
        for (ArrayList i:results){
            //计算各个币种余额统计
            String valueString = new String();
            if(!i.get(3).equals("-")) {
                if (values.containsKey(i.get(3))) {
                    values.put(i.get(3).toString(), values.get(i.get(3)).add((BigDecimal)i.get(4)));
                } else{
                    values.put(i.get(3).toString(), (BigDecimal) i.get(4));
                }
            }

            if(!i.get(5).equals("-")) {
                if (values.containsKey(i.get(5))) {
                    values.put(i.get(5).toString(), values.get(i.get(5)).add((BigDecimal)i.get(6)));
                }else {
                    values.put(i.get(5).toString(), (BigDecimal) i.get(6));
                }
            }

            for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
                valueString = valueString + entry.getKey() + ":" + entry.getValue().toPlainString() + "\r\n";
            }
            i.set(7, valueString);

            //时间格式处理
            Date date = new Date(i.get(0).toString());
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd ahh:mm:ss",Locale.CHINESE);
            i.set(0,ft.format(date));
        }

        //添加表头
        ArrayList<Object> sum0Head = new ArrayList<>();
        sum0Head.add("时间");
        sum0Head.add("操作明细");
        sum0Head.add("操作类型");
        sum0Head.add("起始币种");
        sum0Head.add("起始数量");
        sum0Head.add("结束币种");
        sum0Head.add("结束数量");
        sum0Head.add("币种余额");
        sum0Head.add("来源地址/目标地址");
        results.add(0,sum0Head);
        return results;
    }

    //生成统计表格
    public List<ArrayList<Object>> generateWorksheetsDetails(ArrayList<String> user){

        //获得表格数据
        List<ArrayList<Object>> fiat = generateFiatCurrency(user);
        List<ArrayList<Object>> c2c = generateC2CCurrency(user);
        List<ArrayList<Object>> recharge = generatrRecharge(user);
        List<ArrayList<Object>> withdraw = generateWithdraw(user);

        //添加表头
        List<ArrayList<Object>> fiatHead = new ArrayList<>();
        ArrayList<Object> fiatHead0 = new ArrayList<>();
        fiatHead0.add("法币交易统计数据");
        ArrayList<Object> fiatHead1 = new ArrayList<>();
        fiatHead1.add("姓名");
        fiatHead1.add("UID");
        fiatHead1.add("时间段");
        fiatHead1.add("买入币种");
        fiatHead1.add("买入数量");
        fiatHead1.add("支出人民币");
        fiatHead1.add("卖出币种");
        fiatHead1.add("卖出数量");
        fiatHead1.add("收入人民币");
        fiatHead.add(0,fiatHead0);
        fiatHead.add(1,fiatHead1);
        fiatHead.addAll(fiat);

        List<ArrayList<Object>> c2cHead = new ArrayList<>();
        ArrayList<Object> c2cHead0 = new ArrayList<>();
        c2cHead0.add("币币交易统计数据");
        ArrayList<Object> c2cHead1 = new ArrayList<>();
        c2cHead1.add("姓名");
        c2cHead1.add("UID");
        c2cHead1.add("时间段");
        c2cHead1.add("交易对");
        c2cHead1.add("买卖方向");
        c2cHead1.add("数量");
        c2cHead1.add("成交额");
        c2cHead.add(0,c2cHead0);
        c2cHead.add(1,c2cHead1);
        c2cHead.addAll(c2c);

        List<ArrayList<Object>> rechargeHead = new ArrayList<>();
        ArrayList<Object> rechargeHead0 = new ArrayList<>();
        rechargeHead0.add("充币记录统计数据");
        ArrayList<Object> rechargeHead1 = new ArrayList<>();
        rechargeHead1.add("姓名");
        rechargeHead1.add("UID");
        rechargeHead1.add("时间段");
        rechargeHead1.add("币种");
        rechargeHead1.add("数量");
        rechargeHead.add(0,rechargeHead0);
        rechargeHead.add(1,rechargeHead1);
        rechargeHead.addAll(recharge);

        List<ArrayList<Object>> withdrawHead = new ArrayList<>();
        ArrayList<Object> withdrawHead0 = new ArrayList<>();
        withdrawHead0.add("提币记录统计数据");
        ArrayList<Object> withdrawHead1 = new ArrayList<>();
        withdrawHead1.add("姓名");
        withdrawHead1.add("UID");
        withdrawHead1.add("时间段");
        withdrawHead1.add("币种");
        withdrawHead1.add("数量");
        withdrawHead1.add("提币地址");
        withdrawHead.add(0,withdrawHead0);
        withdrawHead.add(1,withdrawHead1);
        withdrawHead.addAll(withdraw);

        List<ArrayList<Object>> results = new ArrayList<>();
        results.addAll(fiatHead);
        results.addAll(c2cHead);
        results.addAll(rechargeHead);
        results.addAll(withdrawHead);

        return results;

    }

    //统计表格子表
    public List<ArrayList<Object>> generateFiatCurrency(ArrayList<String> user){

        //法币交易
        List<ArrayList<Object>> fiatCurrency = new ArrayList<>();
        //交易金额和数量
        Map<String, ArrayList<BigDecimal>> valuesIn = new LinkedHashMap<>();
        Map<String, ArrayList<BigDecimal>> valuesOut = new LinkedHashMap<>();

        for (ArrayList i:dataList1) {
            if (!i.get(8).equals(user.get(0))) {
                continue;
            }
            if (i.get(9).equals(("买"))) {
                ArrayList<BigDecimal> list = new ArrayList<>();
                if (valuesIn.containsKey(i.get(3))) {
                    list.add(0, valuesIn.get(i.get(3)).get(0).add((BigDecimal) i.get(4)));
                    list.add(1, valuesIn.get(i.get(3)).get(1).add((BigDecimal) i.get(0)));
                    valuesIn.put(i.get(3).toString(), list);
                } else {
                    list.add(0, (BigDecimal) i.get(4));
                    list.add(1, (BigDecimal) i.get(0));
                    valuesIn.put(i.get(3).toString(), list);
                }
            } else if (i.get(9).equals(("卖"))) {
                ArrayList<BigDecimal> list = new ArrayList<>();
                if (valuesOut.containsKey(i.get(3))) {
                    list.add(0, valuesOut.get(i.get(3)).get(0).add((BigDecimal) i.get(4)));
                    list.add(1, valuesOut.get(i.get(3)).get(1).add((BigDecimal) i.get(0)));
                    valuesOut.put(i.get(3).toString(), list);
                } else {
                    list.add(0, (BigDecimal) i.get(4));
                    list.add(1, (BigDecimal) i.get(0));
                    valuesOut.put(i.get(3).toString(), list);
                }

            }
        }
        //币种集合
        List<String> keyIn = new ArrayList<>(valuesIn.keySet());
        List<String> keyOut = new ArrayList<>(valuesOut.keySet());
        List<String> key0 = new ArrayList<>(valuesIn.keySet());
        key0.addAll(keyIn);
        key0.addAll(keyOut);
        List<String> key = new ArrayList<>(valuesIn.keySet());
        for (String i:key0) {
            if(!key.contains(i)){
                key.add(i);
            }
        }
        //时间
        Map<String, ArrayList<String>> time = new LinkedHashMap<>();
        //计算时间段
        for (String i:key) {
            ArrayList<String> timeData = new ArrayList<>();
            for (ArrayList<Object> j:dataList1) {
                if(j.get(3).toString().equals(i) && j.get(8).equals(user.get(0))){
                    timeData.add(j.get(5).toString());
                }
            }
            Collections.sort(timeData);//时间集合排序
            time.put(i,timeData);
        }
        //数据整合
        int count = 0;
        int maxSize = valuesIn.size() > valuesOut.size() ? valuesIn.size() : valuesOut.size();
        for (int j = 0; j < maxSize; j++) {
            ArrayList<Object> result = new ArrayList<>();
            result.add(user.get(1));
            result.add(user.get(0));
            result.add(time.get(key.get(count)).get(0) + "~" + time.get(key.get(count)).get(time.get(key.get(count)).size() - 1));//时间段
            result.add(keyIn.get(count));
            result.add(valuesIn.get(keyIn.get(count)).get(0));
            result.add(valuesIn.get(keyIn.get(count)).get(1));
            result.add(keyOut.get((count)));
            result.add(valuesOut.get(keyOut.get(count)).get(0));
            result.add(valuesOut.get(keyOut.get(count)).get(1));
            fiatCurrency.add(result);
            count++;
        }
        return fiatCurrency;
    }

    public List<ArrayList<Object>> generateC2CCurrency(ArrayList<String> user){

        //币币交易
        List<ArrayList<Object>> c2c = new ArrayList<>();
        //金额
        Map<String, ArrayList<BigDecimal>> valuesIn = new TreeMap<>();
        Map<String, ArrayList<BigDecimal>> valuesOut = new TreeMap<>();
        //计算金额、数量汇总
        for (ArrayList i:dataList3) {
            if(!i.get(4).equals(user.get(0))){
                continue;
            }
            if(i.get(5).equals("买")){
                ArrayList<BigDecimal> list = new ArrayList<>();
                if(valuesIn.containsKey(i.get(6))){
                    list.add(0,valuesIn.get(i.get(6)).get(0).add((BigDecimal) i.get(2)));
                    list.add(1,valuesIn.get(i.get(6)).get(1).add((BigDecimal) i.get(1)));
                    valuesIn.put(i.get(6).toString(), list);
                }else {
                    list.add(0, (BigDecimal)i.get(2));
                    list.add(1, (BigDecimal)i.get(1));
                    valuesIn.put(i.get(6).toString(), list);
                }
            }else {
                ArrayList<BigDecimal> list = new ArrayList<>();
                if(valuesOut.containsKey(i.get(6))){
                    list.add(0,valuesOut.get(i.get(6)).get(0).add((BigDecimal) i.get(2)));
                    list.add(1,valuesOut.get(i.get(6)).get(1).add((BigDecimal) i.get(1)));
                    valuesOut.put(i.get(6).toString(), list);
                }else {
                    list.add(0, (BigDecimal)i.get(2));
                    list.add(1, (BigDecimal)i.get(1));
                    valuesOut.put(i.get(6).toString(), list);
                }
            }
        }
        //交易对
        ArrayList<String> keyIn = new ArrayList<>(valuesIn.keySet());
        ArrayList<String> keyOut = new ArrayList<>(valuesOut.keySet());
        //时间
        Map<String, ArrayList<String>> timeIn = new LinkedHashMap<>();
        Map<String, ArrayList<String>> timeOut = new LinkedHashMap<>();
        //计算时间段
        for (String i:keyIn) {
            ArrayList<String> timeData = new ArrayList<>();
            for (ArrayList<Object> j:dataList3) {
                if(j.get(6).toString().equals(i) && j.get(5).equals("买") && j.get(4).equals(user.get(0))){
                    timeData.add(j.get(7).toString());
                }
            }
            Collections.sort(timeData);
            timeIn.put(i,timeData);
        }
        for (String i:keyOut) {
            ArrayList<String> timeData = new ArrayList<>();
            for (ArrayList<Object> j:dataList3) {
                if(j.get(6).toString().equals(i) && j.get(5).equals("卖") && j.get(4).equals(user.get(0))){
                    timeData.add(j.get(7).toString());
                }
            }
            Collections.sort(timeData);
            timeOut.put(i,timeData);
        }
        //数据整合
        for (String i:valuesIn.keySet()) {
            ArrayList<Object> result = new ArrayList<>();
            result.add(user.get(1));
            result.add(user.get(0));
            result.add(timeIn.get(i).get(0) + "~" + timeIn.get(i).get(timeIn.get(i).size() - 1));//时间段
            result.add(i);
            result.add("买");
            result.add(valuesIn.get(i).get(0));
            result.add(valuesIn.get(i).get(1));
            c2c.add(result);
        }
        for (String i:valuesOut.keySet()) {
            ArrayList<Object> result = new ArrayList<>();
            result.add(user.get(1));
            result.add(user.get(0));
            result.add(timeOut.get(i).get(0) + "~" + timeOut.get(i).get(timeOut.get(i).size() - 1));
            result.add(i);
            result.add("卖");
            result.add(valuesOut.get(i).get(0));
            result.add(valuesOut.get(i).get(1));
            c2c.add(result);
        }
        //按币种排序
        sort(c2c,3);
        return c2c;
    }

    public List<ArrayList<Object>> generatrRecharge(ArrayList<String> user){

        //充币
        List<ArrayList<Object>> recharge = new ArrayList<>();
        //交易量
        Map<String, BigDecimal> value = new LinkedHashMap<>();

        for (ArrayList i:dataList2) {
            if(!i.get(2).equals(user.get(0))){
                continue;
            }
            if(i.get(3).equals("充币")){

                if(value.containsKey(i.get(4))){
                    value.put(i.get(4).toString(),value.get(i.get(4)).add((BigDecimal) i.get(5)));
                }else {
                    value.put(i.get(4).toString(),(BigDecimal) i.get(5));
                }
            }
        }
        //币种
        List<String> key = new ArrayList<>(value.keySet());
        //时间
        HashMap<String, ArrayList<String>> time = new LinkedHashMap<>();
        //计算时间段
        for (String i:key) {
            ArrayList<String> timeData = new ArrayList<>();
            for (ArrayList<Object> j:dataList2){
                if(j.get(4).equals(i) && j.get(3).equals("充币") && j.get(2).equals(user.get(0))){
                    timeData.add(j.get(6).toString());
                }
            }
            Collections.sort(timeData);
            time.put(i,timeData);
        }
        //数据整合
        for (String i:value.keySet()) {
            ArrayList<Object> result = new ArrayList<>();
            result.add(user.get(1));
            result.add(user.get(0));
            result.add(time.get(i).get(0) + "~" + time.get(i).get(time.get(i).size() - 1));
            result.add(i);
            result.add(value.get(i));
            recharge.add(result);
        }
        return recharge;
    }

    public List<ArrayList<Object>> generateWithdraw(ArrayList<String> user){

        //提币
        List<ArrayList<Object>> withdraw = new ArrayList<>();
        //交易量
        Map<ArrayList<String>,BigDecimal> data = new LinkedHashMap<>();
        for (ArrayList i:dataList2) {
            if(!i.get(2).equals(user.get(0))){
                continue;
            }
            if(i.get(3).equals("提币")){
                ArrayList<String> key = new ArrayList<>();
                key.add(i.get(4).toString());
                key.add(i.get(1).toString());
                if(data.keySet().contains(key)){
                    data.put(key,data.get(key).add((BigDecimal) i.get(5)));
                }else {
                    data.put(key,(BigDecimal) i.get(5));
                }
            }
        }
        //地址/币种对
        List<ArrayList<String>> keyList = new ArrayList<>(data.keySet());
        //时间
        Map<ArrayList<String>, ArrayList<String>> time = new LinkedHashMap<>();
        //计算时间段
        for (ArrayList i:keyList){
            ArrayList<String> timeData = new ArrayList<>();
            for (ArrayList j:dataList2) {
                if(i.get(0).equals(j.get(4)) && i.get(1).equals(j.get(1)) && j.get(2).equals(user.get(0))){
                    timeData.add(j.get(6).toString());
                }
            }
            Collections.sort(timeData);
            time.put(i,timeData);
        }
        //数据整合
        for (ArrayList i:keyList){
            ArrayList<Object> result = new ArrayList<>();
            result.add(user.get(1));
            result.add(user.get(0));
            result.add(time.get(i).get(0) + "~" + time.get(i).get(time.get(i).size() - 1));
            result.add(i.get(0));
            result.add(data.get(i));
            result.add(i.get(1));
            withdraw.add(result);
        }
        //按币种排序
        sort(withdraw,3);
        return withdraw;
    }

    //对表格解析出的List按第i列排序
    public void sort(List<ArrayList<Object>> list,Integer i){
        Collections.sort(list, new Comparator<ArrayList<Object>>() {
            @Override
            public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                return o1.get(i).toString().compareTo(o2.get(i).toString());
            }
        });
    }
}
