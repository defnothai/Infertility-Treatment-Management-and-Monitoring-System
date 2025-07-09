package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream().map(appt -> new AppointmentResponse(
                appt.getId(),
                appt.getTime(),
                appt.getStartTime(),
                appt.getEndTime(),
                appt.getStatus(),
                appt.getNote(),
                appt.getCreateAt(),
                appt.getPatientName(),
                appt.getUser() != null ? appt.getUser().getId() : null
        )).collect(Collectors.toList());
    }
}
