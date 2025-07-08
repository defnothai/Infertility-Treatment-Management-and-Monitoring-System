package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentSessionRequest {
    private LocalDate date;
    private String diagnosis;
    private String symptoms;
    private String notes;
}
