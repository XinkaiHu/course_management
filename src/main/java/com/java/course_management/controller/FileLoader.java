package com.java.course_management.controller;


import com.java.course_management.model.AllAttributes;
import com.java.course_management.model.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
public class FileLoader {

    @Resource
    JdbcTemplate jdbcTemplate;

    @PostMapping("upload")
    public Result uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        fileName.replaceAll(" ", "");
        String path =  ResourceUtils.getURL("classpath:").getPath() ;

        File dir = new File(path+"/upload");
        if(!dir.exists()){
            boolean mkdirs = dir.mkdirs();
        }
        File realFile = new File(dir + "/" + fileName);
        if (!realFile.exists()) {
            file.transferTo(realFile);
        }
        String picUrl = "http://192.168.1.178:8080/upload/" + fileName;

        return Result.ok(picUrl);
    }

    /**
     * 上传头像
     * @param studentId
     * @param photo
     * @return
     */
    @PostMapping("updatePhoto")
    public Result updatePhoto(@RequestBody AllAttributes allAttributes) {
        String sql = "update student set photo=? where studentId=?;";
        Object[] args = new Object[] {allAttributes.getPhoto().substring(allAttributes.getPhoto().lastIndexOf("/") + 1), allAttributes.getStudentId()};
        int result = jdbcTemplate.update(sql, args);
        System.out.println("Args: " + Arrays.toString(args));
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * 获取文件路径
     * @param studentId
     * @return
     */
    @GetMapping("getPhoto")
    public Result getPhoto(String studentId) {
        String sql = "select photo from student where studentId=?;";
        Object[] args = new Object[] {studentId};
        String fileName = jdbcTemplate.queryForObject(sql, String.class, args);
        return Result.ok("http://192.168.1.178:8080/upload/" + fileName);
    }

    @GetMapping("download")
    public ResponseEntity<byte[]> downloadFile(String filePath) throws IOException {
        File file = new File("C:\\Users\\XinkaiHu\\Documents\\_programming\\course_management\\target\\classes\\upload\\" + filePath);
        byte[] data = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", new String(file.getName().getBytes(),"iso8859-1"));
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
