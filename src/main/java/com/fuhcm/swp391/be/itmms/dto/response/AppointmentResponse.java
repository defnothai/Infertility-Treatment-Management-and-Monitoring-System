package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentResponse {
    private Long id;
    private String patientName;
    private String phoneNumber;
    private String message;
    private String gender;
    private LocalDate dob;


    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getId();
        this.patientName = appointment.getPatientName();
        this.phoneNumber = appointment.getPhoneNumber();
        this.message = appointment.getMessage();
        this.gender = appointment.getGender().toString();
        this.dob = appointment.getDob();
    }
}
