package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProfileResponse {
    private String userName;
    private LocalDateTime dateOfBirth;
    private Gender gender;
    private String identityNumber;
    private String nationality;
    private String insuranceNumber;
    private String role;
    private String address;
}
