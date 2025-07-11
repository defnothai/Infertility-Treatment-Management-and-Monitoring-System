package com.fuhcm.swp391.be.itmms.validation;

import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Validation {

    public void validateAppointmentRequest(AppointmentRequest appointmentRequest){
        if(appointmentRequest.getDate() == null || appointmentRequest.getTime() == null){
            throw new IllegalArgumentException("Date and Time is null");
        }

        if(appointmentRequest.getPatientName() == null || appointmentRequest.getPatientName().isBlank()){
            throw new IllegalArgumentException("Patient name is null");
        }

        if(appointmentRequest.getPhoneNumber() == null || appointmentRequest.getPhoneNumber().isBlank()){
            throw new IllegalArgumentException("Phone number is null");
        }
    }
}
