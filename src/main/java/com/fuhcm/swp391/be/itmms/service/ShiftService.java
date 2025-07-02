package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<LocalTime> generateSlot(LocalTime start, LocalTime end, Duration slotDuration) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = start;
        while(!current.isAfter(end.minus(slotDuration))) {
            slots.add(current);
            current = current.plus(slotDuration);
        }
        return slots;
    }

    public Shift findMatchingShift(LocalTime time){
        return shiftRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(time, time)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));
    }

    public void checkTimeConflict(Long doctorId, LocalDate date, LocalTime time){
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctorId, date);
        for(Appointment appointment : appointments){
            if((appointment.getStartTime().isBefore(time) && appointment.getEndTime().isAfter(time)) || appointment.getStartTime().equals(time) || appointment.getEndTime().equals(time)){
                throw new IllegalArgumentException("Time is already booked");
            }
        }
    }
}
