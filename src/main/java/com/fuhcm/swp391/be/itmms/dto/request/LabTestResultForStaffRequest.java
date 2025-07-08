package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import lombok.Data;

@Data
public class LabTestResultForStaffRequest {

    private String resultSummary;
    private String resultDetails;
    private LabTestResultStatus status;
    private String notes;

}
