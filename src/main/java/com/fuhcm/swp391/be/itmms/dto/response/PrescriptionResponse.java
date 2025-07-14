package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionResponse {
    private Long id;
    private LocalDate createAt;
    private String notes;
    private List<MedicationPrescriptionResponse> medications;
}
