package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserMedicalRecordResponse {

    private Long id; // id của medical record
    // thông tin bác sĩ
    private String doctorFullName;
    private String doctorEmail;
    private String doctorPosition;
    private String doctorImgUrl;
    // thông tin bệnh án ban đầu
    private String diagnosis;
    private String symptoms;
    private List<LabTestResultResponse> initLabTestResults;
    private List<UltrasoundResponse> initUltrasounds;

}
