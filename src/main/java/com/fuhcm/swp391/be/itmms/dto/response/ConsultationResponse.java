package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.entity.Consultation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {
    private Long id;
    private String patientName;
    private String phoneNumber;
    private String email;
    private String message;

    public ConsultationResponse(String patientName, String phoneNumber, String email, String message) {
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.message = message;
    }

    public ConsultationResponse(Consultation consultation) {
        this.id = consultation.getId();
        this.patientName = consultation.getPatientName();
        this.phoneNumber = consultation.getPhoneNumber();
        this.email = consultation.getEmail();
        this.message = consultation.getMsg();
    }
}
