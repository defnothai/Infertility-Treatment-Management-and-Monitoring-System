package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ProfileResponse {
    private String userName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String identityNumber;
    private String nationality;
    private String insuranceNumber;
    private List<String> roles;
    private String address;
}
