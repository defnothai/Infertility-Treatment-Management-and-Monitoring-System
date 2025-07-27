package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class MedicalRecordSummaryResponse {

    private Long id;
    private String symptoms;
    private String diagnosis;
    private LocalDate createdAt;
    private Long accountId;   // account id của bệnh nhân

    public MedicalRecordSummaryResponse(MedicalRecord medicalRecord) {
        this.id = medicalRecord.getId();
        this.symptoms = medicalRecord.getSymptoms();
        this.diagnosis = medicalRecord.getDiagnosis();
        this.createdAt = medicalRecord.getCreatedAt();
        this.accountId = medicalRecord.getUser().getId();
    }
}
