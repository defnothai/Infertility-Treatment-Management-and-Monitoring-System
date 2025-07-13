package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerMedicalRecordResponse {

    private MedicalRecordResponse medicalRecord;
    private DoctorResponse doctor;

}
