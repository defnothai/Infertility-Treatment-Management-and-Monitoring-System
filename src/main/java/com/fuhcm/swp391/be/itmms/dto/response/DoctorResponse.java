package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {

    private Long id;
    private String fullName;
    private String position;
    private String achievement;
    private String imgUrl;
    private String slug;

    public DoctorResponse(String fullName, String position, String achievement, String imgUrl, String slug) {
        this.fullName = fullName;
        this.position = position;
        this.achievement = achievement;
        this.imgUrl = imgUrl;
        this.slug = slug;
    }

    public DoctorResponse(Long id, String fullName){
        this.id = id;
        this.fullName = fullName;
    }
}
