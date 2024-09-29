package com.myspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiehang
 * @create 2022-07-30 17:20
 */
@RestController
@RequestMapping("/file")
public class FileController {


    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/upload/multipart")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> uploadMultipartFile(MultipartFile file){


        //得到上传文件的文件名
        String fileName = file.getOriginalFilename();
        File dest=new File("D:/file/"+fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return dealResultMap(false,fileName+"文件上传失败");
        }

        return dealResultMap(true, fileName+"文件上传成功");
    }


    @PostMapping("/upload/part")
    @ResponseBody
    public Map<String, Object> uploadPart(@RequestParam("file") Part part) {
        String fileName = part.getSubmittedFileName();
        try {
            part.write("D:/file/"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return dealResultMap(false,fileName+"文件上传失败");
        }
        return dealResultMap(true, fileName+"文件上传成功");
    }


    /**
     * 处理上传文件结果
     * @param success
     * @param message
     * @return
     */
    private Map<String,Object> dealResultMap(boolean success,String message) {
        Map<String, Object> map=new HashMap<>();
        map.put("flag", success);
        map.put("message", message);
        return map;
    }

}
