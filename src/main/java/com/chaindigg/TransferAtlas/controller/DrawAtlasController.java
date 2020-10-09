package com.chaindigg.TransferAtlas.controller;

import com.chaindigg.TransferAtlas.model.AjaxResponse;
import com.chaindigg.TransferAtlas.model.StatusCode;
import com.chaindigg.TransferAtlas.service.DrawAtlasService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class DrawAtlasController {

    @Resource
    private DrawAtlasService drawAtlasService;

    @GetMapping("/drawAtlas")
    public String forward(){
        return "drawAtlas.html";
    }

    @ResponseBody
    @PostMapping("/dealDrawData")
    public AjaxResponse drawAtlas(MultipartFile[] selectFiles, String min, String max, String identification) {
        AjaxResponse ajaxResponse = null;
        try {
            ajaxResponse = drawAtlasService.dealData(selectFiles, min, max, identification);
        } catch (IOException e) {
            return AjaxResponse.fail(StatusCode.S3.code, StatusCode.S3.message);
        }
        return ajaxResponse;
    }
}
