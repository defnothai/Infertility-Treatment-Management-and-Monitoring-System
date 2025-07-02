package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Account;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long doctorId;
    private LocalDate date;
    private LocalTime time;
    private String message;
    private String patientName;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dob;
}
