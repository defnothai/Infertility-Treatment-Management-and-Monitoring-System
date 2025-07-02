package com.fuhcm.swp391.be.itmms.dto;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class PatientInfo {

    private Long id;
    private String email;
    private String fullName;
    private Gender gender;
    private String phoneNumber;
}




