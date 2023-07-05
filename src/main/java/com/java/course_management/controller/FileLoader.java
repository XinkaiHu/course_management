package com.java.course_management.controller;


import com.java.course_management.model.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@CrossOrigin(origins = "*")
public class FileLoader {

    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String path =  ResourceUtils.getURL("classpath:").getPath() ;


        File dir = new File(path+"/upload");
        if(!dir.exists()){
            boolean result = dir.mkdir();
            if (!result) {
                return Result.fail("Error");
            }
        }
        File realFile = new File(dir + "/" + fileName);
        if (!realFile.exists()) {
            file.transferTo(realFile);
        }
        String picUrl = "http://localhost:8888/upload/" + fileName;
        return Result.ok(picUrl);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(String filePath) throws IOException {
        File file = new File("classpath:");
        byte[] data = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", new String(file.getName().getBytes(),"iso8859-1"));
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
