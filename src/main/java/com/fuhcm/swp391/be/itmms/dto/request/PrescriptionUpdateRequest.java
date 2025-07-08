package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionUpdateRequest {

    private String notes;
    private List<MedicationPrescriptionRequest> medications;

}
