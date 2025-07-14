package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
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
    private String labTestName;
    private String staffFullName;
}
