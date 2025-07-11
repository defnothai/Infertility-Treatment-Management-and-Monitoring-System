package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HospitalAchievementRequest {

    private String title;
    private String description;
    private LocalDate achievedAt;
    private String imgUrl;

}
