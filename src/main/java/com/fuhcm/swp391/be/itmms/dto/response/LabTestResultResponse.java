package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabTestResultResponse {

    private Long id;
    private LocalDate testDate;
    private String resultSummary;
    private String resultDetails;
    private LabTestResultStatus status;
    private String notes;
    private LabTestResultType labTestType;
    private String labTestName;
    private Long labTestId;
    private String staffFullName;
    private Long staffAccountId;


}
