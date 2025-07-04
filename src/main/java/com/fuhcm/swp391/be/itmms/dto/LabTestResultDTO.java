package com.fuhcm.swp391.be.itmms.dto;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.response.AccountBasic;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import jakarta.persistence.*;
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
