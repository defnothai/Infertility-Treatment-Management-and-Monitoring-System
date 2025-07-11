package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private LocalDate time;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String note;
    private LocalDateTime createAt;
    private String patientName;
    private String phoneNumber;
    private String message;
    private String gender;
    private LocalDate dob;
    private Long user;


    public AppointmentResponse(Appointment appointment) {
        this.patientName = appointment.getPatientName();
        this.phoneNumber = appointment.getPhoneNumber();
        this.gender = appointment.getGender().toString();
        this.dob = appointment.getDob();
    }

    public AppointmentResponse(Long id, LocalDate time,  LocalTime startTime, LocalTime endTime, AppointmentStatus status, String note, LocalDateTime createAt, String patientName, Long userId) {
        this.id = id;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
        this.createAt = createAt;
        this.patientName = patientName;
        this.user = userId;
    }
}
