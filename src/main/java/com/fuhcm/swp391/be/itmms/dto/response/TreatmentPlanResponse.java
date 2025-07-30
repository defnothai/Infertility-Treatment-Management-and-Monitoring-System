package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TreatmentPlanResponse {

    private Long id;
    private LocalDate dayStart;
    private LocalDate dayEnd;
    private TreatmentPlanStatus status;
    private String serviceName;
    private String notes;
    private List<TreatmentStageProgressResponse> treatmentStageProgressResponses;


}
