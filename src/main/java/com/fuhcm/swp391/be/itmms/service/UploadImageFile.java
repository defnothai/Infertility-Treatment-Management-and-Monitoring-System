package com.fuhcm.swp391.be.itmms.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UploadImageFile {

    String uploadImage(MultipartFile file) throws IOException;
    List<String> uploadImages(List<MultipartFile> files) throws IOException;

}
