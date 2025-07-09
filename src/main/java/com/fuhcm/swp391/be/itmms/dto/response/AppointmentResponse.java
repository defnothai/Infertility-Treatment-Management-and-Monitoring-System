package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDate createAt;
    private String patientName;
    private Long accountPatientId;
}
