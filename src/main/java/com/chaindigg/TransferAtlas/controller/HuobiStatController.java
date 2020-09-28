package com.chaindigg.TransferAtlas.controller;

import com.chaindigg.TransferAtlas.service.HuobiStatService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HuobiStatController {

    @ResponseBody
    @PostMapping("/huobiStat")
    public List huobiStat(MultipartFile selectFile){
        HuobiStatService huobiStatService = new HuobiStatService();
        Workbook workbook = null;
        try {
            workbook = huobiStatService.readWorkbookFromLocalFile(selectFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List sum = huobiStatService.outputWorkbook(workbook);
        return sum;
    }
}
