package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {

    private Long sessionId;
    private String notes;
    List<MedicationPrescriptionRequest> medications;

}
