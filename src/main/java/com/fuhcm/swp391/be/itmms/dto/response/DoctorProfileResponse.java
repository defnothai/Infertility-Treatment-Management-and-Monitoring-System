package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class DoctorProfileResponse extends ProfileResponse{
    private String expertise;
    private String position;
    private String achievements;

    public DoctorProfileResponse(String userName, String email, String phoneNumber, String expertise, String position, String achievements) {
        super(userName, email, phoneNumber);
        this.expertise = expertise;
        this.position = position;
        this.achievements = achievements;
    }
}
