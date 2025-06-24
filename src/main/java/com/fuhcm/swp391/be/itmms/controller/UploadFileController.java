package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.service.UploadImageFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    public UploadFileController(UploadImageFile uploadImageFile) {
        this.uploadImageFile = uploadImageFile;
    }

    @PostMapping("/image")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadImageFile.uploadImage(file);
    }

}
