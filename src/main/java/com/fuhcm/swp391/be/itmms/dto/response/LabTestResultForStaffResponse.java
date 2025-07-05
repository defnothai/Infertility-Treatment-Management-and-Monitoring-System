package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import lombok.Data;

import java.time.LocalDate;
@Data
public class LabTestResultForStaffResponse {

    private Long id;
    private LocalDate testDate;
    private String resultSummary;
    private String resultDetails;
    private LabTestResultStatus status;
    private String notes;
    private String labTestName;
    private String patientFullName;
    private String patientDob;
    private String patientPhoneNumber;
    private Gender patientGender;

}
