package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlogPostRequest {

    @NotBlank(message = "Title không được để trống")
    private String title;

    @NotBlank(message = "Content không được để trống")
    private String content;
}
