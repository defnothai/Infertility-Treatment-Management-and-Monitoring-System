package com.fuhcm.swp391.be.itmms.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiagnosisSymptom {

    private String diagnosis;
    private String symptoms;

}
