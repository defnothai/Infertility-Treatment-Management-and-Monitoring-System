package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import lombok.Data;

import java.util.List;

@Data
public class LabTestResultRequest {

    private long medicalRecordId;
    private List<LabTestDTO> tests;
}
