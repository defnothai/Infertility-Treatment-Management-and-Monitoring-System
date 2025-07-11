package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.MedicationRoute;
import lombok.Data;

@Data
public class MedicationPrescriptionResponse {
    private Long id;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String usageInstruction;
    private MedicationRoute route;
    private Integer quantity;
}
