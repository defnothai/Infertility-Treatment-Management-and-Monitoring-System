package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.MedicationRoute;
import lombok.Data;

@Data
public class MedicationPrescriptionRequest {
    private Long medicationId;
    private Integer quantity;
    private String dosage;
    private String frequency;
    private String usageInstruction;
    private MedicationRoute route;
}
