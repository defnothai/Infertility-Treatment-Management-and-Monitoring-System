package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalRecordAccessResponse {

    private Long id;
    private LocalDate dayStart;
    private LocalDate dayEnd;

}
