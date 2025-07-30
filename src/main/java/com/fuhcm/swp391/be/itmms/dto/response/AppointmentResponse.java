package com.fuhcm.swp391.be.itmms.dto.response;

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
    private String status;
    private String note;
    private LocalDate createAt;
    private String patientName;
    private String phoneNumber;
    private String message;
    private String gender;
    private LocalDate dob;
    private Long userId; // id của account
    private String doctorName;

    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getId();
        this.time = appointment.getTime();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
        this.status = appointment.getStatus().name();
        this.note = appointment.getNote();
        this.createAt = appointment.getCreateAt().toLocalDate();
        this.patientName = appointment.getPatientName();
        this.phoneNumber = appointment.getPhoneNumber();
        this.message = appointment.getMessage();
        this.gender = appointment.getGender() != null ? appointment.getGender().toString() : null;
        this.dob = appointment.getDob();
        this.user = appointment.getUser().getId();
    }

    public AppointmentResponse(Long id, LocalDate time,  LocalTime startTime, LocalTime endTime, AppointmentStatus status, String note, LocalDateTime createAt, String patientName, Long userId) {
        this.id = id;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status.name();
        this.note = note;
        this.createAt = createAt.toLocalDate();
        this.patientName = patientName;
        this.userId = userId;
    }
}
