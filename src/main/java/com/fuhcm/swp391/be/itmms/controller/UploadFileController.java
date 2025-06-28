package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.UploadImageFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    public UploadFileController(UploadImageFile uploadImageFile) {
        this.uploadImageFile = uploadImageFile;
    }

    @PostMapping("/api/upload-image")
    public ResponseEntity uploadFile(@RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "UPLOAD_IMAGE_SUCCESS",
                                                        "Tải ảnh lên thành công",
                                                            uploadImageFile.uploadImage(file)));
    }

}
