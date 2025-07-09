package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TreatmentPlanResponse {

    private Long id;
    private LocalDate dateStart;
    private String serviceName;
    private List<TreatmentStageProgressResponse> treatmentStageProgressResponses;


}
