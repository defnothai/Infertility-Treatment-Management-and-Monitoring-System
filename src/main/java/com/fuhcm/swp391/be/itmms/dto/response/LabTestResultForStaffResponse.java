package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public LabTestResultForStaffResponse() {
    }

    public LabTestResultForStaffResponse(LabTestResult labTestResult) {
        this.id = labTestResult.getId();
        this.testDate = labTestResult.getTestDate();
        this.resultSummary = labTestResult.getResultSummary();
        this.resultDetails = labTestResult.getResultDetails();
        this.status = labTestResult.getStatus();
        this.notes = labTestResult.getNotes();
        this.labTestName = labTestResult.getTest().getName();
        this.patientFullName = labTestResult.getMedicalRecord().getUser().getAccount().getFullName();
        this.patientDob = labTestResult.getMedicalRecord().getUser().getDob().toString();
        this.patientPhoneNumber = labTestResult.getMedicalRecord().getUser().getAccount().getPhoneNumber();
        this.patientGender = labTestResult.getMedicalRecord().getUser().getAccount().getGender();

    }

}
