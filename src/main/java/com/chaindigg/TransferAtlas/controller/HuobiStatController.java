package com.chaindigg.TransferAtlas.controller;

import com.chaindigg.TransferAtlas.model.Response;
import com.chaindigg.TransferAtlas.service.HuobiStatService;
import com.chaindigg.TransferAtlas.utils.MultipartFileToFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
public class HuobiStatController {

    @ResponseBody
    @PostMapping("/huobiStat")
    public Response dealFile(MultipartFile selectFile){
        HuobiStatService huobiStatService = new HuobiStatService();
        Workbook workbook = null;
        try {
            workbook = huobiStatService.readWorkbookFromLocalFile(selectFile);
            File file = MultipartFileToFile.multipartFileToFile(selectFile);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = huobiStatService.outputWorkbook(workbook);
        stopWatch.stop();
        //打印任务耗时
        System.out.println("总计耗时" + stopWatch.getLastTaskTimeMillis() + "ms");
        return response;
    }
}
