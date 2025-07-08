package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentStageProgressResponse {

    private Long id;
    private LocalDate dateStart;
    private LocalDate dateComplete;
    private String notes;
    private TreatmentStageStatus status;
    private String stageName;
}
