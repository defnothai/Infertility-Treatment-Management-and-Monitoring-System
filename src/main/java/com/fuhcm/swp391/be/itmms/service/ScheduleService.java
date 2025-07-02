package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ShiftService shiftService;

    public List<Schedule> getSchedules(Long id) {
        if(id <=0){
            throw new IllegalArgumentException("Invalid account ID: must be a positive number");
        }
        return scheduleRepository.findByAssignToId(id);
    }

    public void generateSchedules(LocalDate startDate, LocalDate endDate, Authentication authentication) {
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findAll();
        Optional<Account> accountOtp = accountRepository.findByEmail(authentication.getName());
        Account account;
        if(accountOtp.isPresent()){
            account = accountOtp.get();
        } else {
            throw new RuntimeException("Account not found");
        }


        for(ScheduleTemplate template : templates){

            LocalDate scheduleStartDate = startDate;

            while(!scheduleStartDate.isAfter(endDate)){
                if(scheduleStartDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())){
                    boolean exists = scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(template.getAccount().getId(), template.getShift().getId().intValue(), scheduleStartDate);
                    if(!exists){
                        Schedule schedule = new Schedule();
                        schedule.setWorkDate(scheduleStartDate);
                        schedule.setRoomNumber(template.getRoomNumber());
                        schedule.setMaxCapacity(template.getMaxCapacity());
                        schedule.setCreateAt(LocalDate.now());
                        schedule.setAppointments(null);
                        schedule.setAssignTo(template.getAccount());
                        schedule.setAccount(account);
                        schedule.setShift(template.getShift());
                        scheduleRepository.save(schedule);
                    }
                }
                scheduleStartDate = scheduleStartDate.plusDays(1);
            }
        }
    }

    public List<LocalDate> getAvailableSchedulesByDoctor(Long id) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth());
        Account doctor = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(doctor.getId(), startDate, endDate);
        if(schedules.isEmpty()){
            System.out.println("Schedules not found");
        }

        List<LocalDate> availableSchedules = new ArrayList<>();
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            if(allSlots.isEmpty()){
                System.out.println("Slot not found");
            }
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
            if(appointments.isEmpty()){
                System.out.println("Appointment not found");
            }
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            boolean hasFreeSlots = allSlots.stream().anyMatch(slot -> !booked.contains(slot));
            if(hasFreeSlots){
                availableSchedules.add(schedule.getWorkDate());
            }
        }
        return availableSchedules;
    }

    public List<LocalTime> getAvailableSlots(Long id, LocalDate date) {
        Account doctor = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<LocalTime> availableSlots = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDate(id, date);
        for(Schedule schedule : schedules){
            List<LocalTime> allSlots = shiftService.generateSlot(schedule.getShift().getStartTime(), schedule.getShift().getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime)){
                    availableSlots.add(slotTime);
                }
            }
        }

        return availableSlots;
    }

    public List<LocalDate> getAvailableDatesByDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth());
        List<LocalDate> availableDates = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByWorkDateBetween(startDate, endDate);
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(schedule.getAssignTo().getId(), schedule.getWorkDate());
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime) && !availableDates.contains(schedule.getWorkDate())){
                    availableDates.add(schedule.getWorkDate());
                }
            }
        }
        Collections.sort(availableDates);
        return availableDates;
    }

    public List<LocalTime>  getAvailableSlotsByDate(LocalDate date) {
        List<LocalTime> availableSlots = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByWorkDate(date);
        if(schedules.isEmpty()){
            throw new RuntimeException("Schedule not found");
        }
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<Appointment>  appointments = appointmentRepository.findByDoctorIdAndTime(schedule.getAssignTo().getId(), schedule.getWorkDate());
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime) && !availableSlots.contains(slotTime)){
                    availableSlots.add(slotTime);
                }
            }
        }
        return availableSlots;
    }

    public Schedule findSchedule(Long doctorId, LocalDate date, int shiftId) {
        Schedule schedule = scheduleRepository.findByAssignToIdAndWorkDateAndShiftId(doctorId, date, shiftId);
        if(schedule == null){
            throw new IllegalArgumentException("Schedule not found");
        }
        return schedule;
    }

}


