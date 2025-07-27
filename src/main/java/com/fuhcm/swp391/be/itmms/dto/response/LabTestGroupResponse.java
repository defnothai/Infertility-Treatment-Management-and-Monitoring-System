package com.fuhcm.swp391.be.itmms.dto.response;


import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LabTestGroupResponse {
    private String patientName;
    private String patientDob;
    private String patientPhoneNumber;
    private LocalDate testDate;
    private LabTestResultStatus status;
    private Integer totalTests;
    private List<Long> labTestResultIds;
}
