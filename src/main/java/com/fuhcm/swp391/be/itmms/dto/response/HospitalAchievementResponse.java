package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HospitalAchievementResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate achievedAt;
    private String imgUrl;
    private String slug;
    private String createdByName;

}
