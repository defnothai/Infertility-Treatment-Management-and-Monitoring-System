package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

@Data
public class MedicationResponse {
    private Long id;
    private String name;
    private String description;
    private String form;
    private String strength;
    private String unit;
    private String manufacturer;
}
