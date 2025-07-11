package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
@Getter
public class UserProfileResponse extends ProfileResponse{
    private LocalDate dateOfBirth;
    private String gender;
    private String identityNumber;
    private String nationality;
    private String insuranceNumber;
    private String address;

    public UserProfileResponse(String userName, String email, String phoneNumber, LocalDate dateOfBirth, String gender, String identityNumber, String nationality, String insuranceNumber, String address) {
        super(userName, email, phoneNumber);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.identityNumber = identityNumber;
        this.nationality = nationality;
        this.insuranceNumber = insuranceNumber;
        this.address = address;
    }
}
