package com.fuhcm.swp391.be.itmms.dto.response;


import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {

    private Long id;
    private String fullName;
    private String expertise;
    private String position;
    private EmploymentStatus status;
    private String achievements;
    private String description;
    private String slug;
    private String imgUrl;

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

    public DoctorResponse(Account account){
        this.id = account.getDoctor().getId();
        this.fullName = account.getFullName();
        this.position = account.getDoctor().getPosition();
    }
}
