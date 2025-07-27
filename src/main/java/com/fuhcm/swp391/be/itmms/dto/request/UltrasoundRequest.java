package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UltrasoundRequest {

    @NotBlank(message = "Kết quả không được để trống")
    private String result;
    @NotNull(message = "Không được để trống ảnh siêu âm")
    private List<String> imageUrls;
    private Long medicalRecordId;

}
