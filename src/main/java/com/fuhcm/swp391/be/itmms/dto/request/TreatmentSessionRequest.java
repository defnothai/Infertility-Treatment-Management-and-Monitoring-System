package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentSessionRequest {
    private String diagnosis;
    private String symptoms;
    private String notes;
}
