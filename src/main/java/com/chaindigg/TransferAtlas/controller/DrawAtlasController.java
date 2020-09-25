package com.chaindigg.TransferAtlas.controller;

import com.chaindigg.TransferAtlas.service.DrawAtlasService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class DrawAtlasController {

    @Resource
    private DrawAtlasService drawAtlasService;

    @ResponseBody
    @PostMapping("/drawAtlas")
    public Map<String, Object> js(MultipartFile[] selectFiles, String min, String max, String identification) {
        Map<String, Object> result = null;
        try {
            result = drawAtlasService.dealData(selectFiles, min, max, identification);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
