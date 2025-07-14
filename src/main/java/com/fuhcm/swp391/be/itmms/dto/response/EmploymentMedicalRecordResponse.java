package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmploymentMedicalRecordResponse {

    private Long medicalRecordId;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String phoneNumber;
    private Long accountId;  // id account của bệnh nhân

}
