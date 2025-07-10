package com.fuhcm.swp391.be.itmms.components;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentStatusScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 600000)
    public void checkUnpaidAppointments(){
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Appointment> unpaidAppointments = appointmentRepository.findByStatusAndCreateAtBefore(AppointmentStatus.UNPAID, oneHourAgo);
        for(Appointment appointment : unpaidAppointments){
            appointment.setStatus(AppointmentStatus.NOT_PAID);
        }
        appointmentRepository.saveAll(unpaidAppointments);
    }

    @Scheduled(fixedRate = 60000)
    public void checkUpcheckingAppointments(){
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Appointment> unCheckingAppointments = appointmentRepository.findByStatusAndTimeLessThanEqual(AppointmentStatus.UNCHECKED_IN, LocalDate.now());

        List<Appointment> appointmentsToCancel = new ArrayList<>();
        for(Appointment appointment : unCheckingAppointments){
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getTime(), appointment.getStartTime());
            if(appointmentDateTime.isBefore(fifteenMinutesAgo)){
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentsToCancel.add(appointment);
            }
        }
        appointmentRepository.saveAll(appointmentsToCancel);

    }
}
