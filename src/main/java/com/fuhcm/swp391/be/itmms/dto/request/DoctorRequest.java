package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import lombok.Data;

@Data
public class DoctorRequest {
    private String expertise;
    private String position;
    private EmploymentStatus status;
    private String achievements;
    private String description;
    private String slug;
    private String imgUrl;
}
