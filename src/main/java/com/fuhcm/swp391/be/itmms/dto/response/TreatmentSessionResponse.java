package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentSessionResponse {

    private Long id;
    private LocalDate date;
    private String diagnosis;
    private String symptoms;
    private String notes;
    private TreatmentSessionStatus status;

}
