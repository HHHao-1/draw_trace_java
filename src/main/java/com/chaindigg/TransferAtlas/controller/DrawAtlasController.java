package com.chaindigg.TransferAtlas.controller;

import com.chaindigg.TransferAtlas.model.Response;
import com.chaindigg.TransferAtlas.model.StatusCode;
import com.chaindigg.TransferAtlas.service.DrawAtlasService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class DrawAtlasController {
    @GetMapping("/vue")
    @ResponseBody
    public String vue(){
        return "成功了";
    }

    @Resource
    private DrawAtlasService drawAtlasService;

    @GetMapping("/drawAtlas")
    public String forward(){
        return "drawAtlas.html";
    }

    @ResponseBody
    @PostMapping("/dealDrawData")
    public Response drawAtlas(@RequestParam MultipartFile[] selectFiles, String min, String max, String identification) {
        Response response = null;
        try {
            response = drawAtlasService.dealData(selectFiles, min, max, identification);
        } catch (IOException e) {
            return response.fail(StatusCode.PARSE_ERROR);
        }
        return response;
    }
}
