package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private List<LabTestResultResponse> initLabTestResults; // k cần dùng cho mana
    private List<UltrasoundResponse> initUltrasounds; // k cần dùng cho mana
    private PermissionLevel level;

}
