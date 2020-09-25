package com.chaindigg.filedeal.controller;

import com.chaindigg.filedeal.service.JsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@ResponseBody
public class Controller {

    @Resource
    private JsService jsService;

    @PostMapping("/js")
    public Map<String, Object> js(MultipartFile[] selectFiles, String min, String max, String identification) {
        return jsService.dealFiles(selectFiles, min, max, identification);
    }
}
