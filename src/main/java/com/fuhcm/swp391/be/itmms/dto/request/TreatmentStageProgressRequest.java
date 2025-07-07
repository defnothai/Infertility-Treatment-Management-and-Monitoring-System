package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentStageProgressRequest {
    
    private LocalDate dateStart;
    private LocalDate dateComplete;
    private String notes;
    private TreatmentStageStatus status;
    private String stageName;
}
