package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.dto.request.WeeklyScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleResponse;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import jakarta.validation.constraints.Min;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<ScheduleResponse> getSchedulesByWeekYear(WeeklyScheduleRequest request, Authentication authentication) {
        Account currentUser = accountRepository.findByEmail(authentication.getName());

        String[] parts = request.getWeekly().split(" to ");
        String[] startParts = parts[0].split("/");
        String[] endParts = parts[1].split("/");

        int year = request.getYear();
        int startDay = Integer.parseInt(startParts[0]);
        int startMonth = Integer.parseInt(startParts[1]);
        int endDay = Integer.parseInt(endParts[0]);
        int endMonth = Integer.parseInt(endParts[1]);

        LocalDate startDate = LocalDate.of(year, startMonth, startDay);
        LocalDate endDate = LocalDate.of(year, endMonth, endDay);

        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(
                currentUser.getId(), startDate, endDate);
        return schedules.stream().map(schedule -> {
            ScheduleResponse dto = modelMapper.map(schedule, ScheduleResponse.class);
            dto.setShiftTime(schedule.getShift().getStartTime() + " - " + schedule.getShift().getEndTime());
            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public void generateStaffSchedules(LocalDate startDate,
                                       LocalDate endDate,
                                       Authentication authentication) throws BadRequestException {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Thời gian nhập không hợp lệ");
        }
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findAll();
        Account account = accountRepository.findByEmail(authentication.getName());

        for (ScheduleTemplate template : templates) {
            LocalDate scheduleStartDate = startDate;
            while (!scheduleStartDate.isAfter(endDate)) {
                if (scheduleStartDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
                    boolean exists = scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
                            template.getAccount().getId(), template.getShift().getId().intValue(), scheduleStartDate);
                    if (!exists) {
                        Schedule schedule = new Schedule();
                        schedule.setWorkDate(scheduleStartDate);
                        schedule.setRoomNumber(template.getRoomNumber());
                        schedule.setMaxCapacity(template.getMaxCapacity());
                        schedule.setCreateAt(LocalDate.now());
                        schedule.setStatus(ScheduleStatus.WORKING);
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

    public void generateSchedulesForTemplate(Long templateId,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             Authentication authentication) throws NotFoundException {
        Account account = accountRepository.findByEmail(authentication.getName());
        ScheduleTemplate template = scheduleTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Template not found"));

        LocalDate currentDate = startDate;
        while (!currentDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
            currentDate = currentDate.plusDays(1);
        }
        while (!currentDate.isAfter(endDate)) {
            if (!scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
                    template.getAccount().getId(), template.getShift().getId().intValue(), currentDate)) {
                Schedule schedule = new Schedule();
                schedule.setWorkDate(currentDate);
                schedule.setRoomNumber(template.getRoomNumber());
                schedule.setMaxCapacity(template.getMaxCapacity());
                schedule.setCreateAt(LocalDate.now());
                schedule.setStatus(ScheduleStatus.WORKING);
                schedule.setAssignTo(template.getAccount());
                schedule.setAccount(account);
                schedule.setShift(template.getShift());
                scheduleRepository.save(schedule);
            }
            currentDate = currentDate.plusDays(7);
        }
    }

    public List<LocalTime> getAvailableSlots(Long id, LocalDate workDate) {
        Account doctor;
        Optional<Account> doctorOpt =  accountRepository.findById(id);
        if (doctorOpt.isPresent()) {
            doctor = doctorOpt.get();
        } else {
            throw new IllegalArgumentException("Doctor not found");
        }
        List<LocalTime> availableSlots = new ArrayList<>();
        List<Schedule>  schedules = scheduleRepository.findByAssignToIdAndWorkDate(id, workDate);
        for (Schedule schedule : schedules) {
            List<LocalTime> allSlots = shiftService.generateSlot(schedule.getShift().getStartTime(), schedule.getShift().getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments) {
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots) {
                if(!booked.contains(slotTime)){
                    availableSlots.add(slotTime);
                }
            }
        }
        Collections.sort(availableSlots);
        return availableSlots;
    }

    public Schedule findSchedule(Long id, LocalDate workDate, int shiftId) {
        Schedule schedule = scheduleRepository.findByAssignToIdAndWorkDateAndShiftId(id, workDate, shiftId);
        if(schedule == null){
            throw new IllegalArgumentException("Schedule not found");
        }
        return schedule;
    }

    public List<LocalDate> getAvailableSchedulesByDoctor(@Min(1) Long id) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth());
        Account doctor = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(doctor.getId(), startDate, endDate);
        List<LocalDate> availableSchedules = new ArrayList<>();
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
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
        Collections.sort(availableSchedules);
        return availableSchedules;
    }


    public List<LocalDate> getAvailableDatesByDate() {
        LocalDate startDate = LocalDate.now().plusDays(1);
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
}

