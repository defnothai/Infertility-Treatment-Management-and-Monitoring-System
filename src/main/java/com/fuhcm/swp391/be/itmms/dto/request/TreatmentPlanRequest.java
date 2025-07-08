package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentPlanRequest {

    private Long medicalRecordId;
    private Long serviceId;

}
