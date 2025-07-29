package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TreatmentPlanUpdateRequest {

    private TreatmentPlanStatus status;
    private String notes;

}
