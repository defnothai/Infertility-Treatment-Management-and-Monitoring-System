package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LabTestResultDTO {

    private LabTestResultType labTestType;
    private String resultSummary;
    private String resultDetails;
    private LabTestResultStatus status;
    private LocalDate testDate;
    private String notes;
    private String testName;
    private String fullNameStaff;
    private String emailStaff;
    private String fullNamePatient;
    private String phoneNumberPatient;


}
