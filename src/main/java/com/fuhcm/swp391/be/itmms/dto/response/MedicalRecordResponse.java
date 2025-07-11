package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRecordResponse {

    private Long id;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
    private LocalDate createdAt;
    private String identityNumber;
    private String nationality;
    private String insuranceNumber;
    private String diagnosis;
    private String symptoms;
    private List<LabTestResultResponse> initLabTestResults;
    private List<UltrasoundResponse> initUltrasounds;

}
