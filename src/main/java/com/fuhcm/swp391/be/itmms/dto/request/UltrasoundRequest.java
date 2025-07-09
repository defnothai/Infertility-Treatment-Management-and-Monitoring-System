package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UltrasoundRequest {

    private String result;
    private List<String> imageUrls;
    private Long medicalRecordId;

}
