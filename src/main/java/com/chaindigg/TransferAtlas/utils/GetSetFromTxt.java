package com.chaindigg.TransferAtlas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;




public class GetSetFromTxt {
    public static Set<String> getSetFromTxt() throws Exception{
        //存放文件内容的set集合
        Set<String> set = null;
        //获取文件的路径
        String path = "src/main/resources/Currencys.txt";
        //读取文件
        File file = new File(path);
        //建立读取流
        InputStreamReader read = new InputStreamReader(new FileInputStream(path),"UTF-8");

        try {
            //判断文件是否存在
            if(file.isFile() && file.exists()){
                //初始化set集合
                set = new HashSet<String>();
                //缓冲区读取流
                BufferedReader bufferedReader = new BufferedReader(read);

                //循环读取文件中内容，每次读取一行内容
                String txt = null;
                while((txt = bufferedReader.readLine()) != null){
                    //读取文件，将文件内容放入到set中
                    set.add(txt);
                }
            }
            else{         //不存在抛出异常信息
                throw new Exception("文件不存在");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            read.close();     //关闭文件流
        }
        return set;
    }
}
