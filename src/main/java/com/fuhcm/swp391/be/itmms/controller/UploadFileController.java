package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.UploadImageFile;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    public UploadFileController(UploadImageFile uploadImageFile) {
        this.uploadImageFile = uploadImageFile;
    }

    @PostMapping("/api/upload-image")
    public ResponseEntity uploadFile(@RequestParam("image") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Không có file ảnh nào được tải lên");
        }
        String url = uploadImageFile.uploadImage(file);
        if (url == null || url.isEmpty()) {
            throw new RuntimeException("Lỗi khi tải ảnh lên");
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "UPLOAD_IMAGE_SUCCESS",
                                                        "Tải ảnh lên thành công",
                                                        url));
    }

    @PostMapping("/api/upload-images")
    public ResponseEntity uploadMultipleImages(@RequestParam("images") List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Không có file ảnh nào được tải lên");
        }
            List<String> urls = uploadImageFile.uploadImages(files);
        if (urls.isEmpty()) {
            throw new RuntimeException("Lỗi khi tải ảnh lên");
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "UPLOAD_IMAGES_SUCCESS",
                "Tải ảnh lên thành công",
                urls));
    }
}

