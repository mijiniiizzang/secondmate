package com.example.secondmate.controller;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageDownloadRestController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/download/{fileName}/{realName}")
    public ResponseEntity<Resource> downloadFile(
        @PathVariable String fileName,
        @PathVariable String realName
    ) {
        // 저장된 파일 경로 확인
        Path imagePath = Paths.get(uploadDir + fileName);
        try {
            Resource resource = new UrlResource(imagePath.toUri());


            if(!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다.");
            }
            
            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .header(HttpHeaders.CONTENT_DISPOSITION,
                                            "attachment; filename=\"" + 
                                            URLEncoder.encode(realName, StandardCharsets.UTF_8) + "\"")
                                 .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 다운로드 실패");
        }
    }
}
