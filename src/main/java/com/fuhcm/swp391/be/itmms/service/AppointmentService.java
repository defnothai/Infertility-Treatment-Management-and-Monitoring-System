package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.JWTFilter;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetailReminder;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.validation.Validation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private Validation validation;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ReminderService reminderService;
    @Autowired
    private EmailService emailService;

    public Appointment createNewAppointment(AppointmentRequest appointmentRequest,
                                            Authentication authentication) {
        Account bookBy = accountService.getAuthenticatedUser(authentication);

        validation.validateAppointmentRequest(appointmentRequest);

        LocalDate workDate = appointmentRequest.getDate();
        LocalTime desired = appointmentRequest.getTime();
        boolean hasAppointmentToday = hasAppointmentToday(bookBy.getId(), workDate);
        if(hasAppointmentToday) {
            throw new IllegalArgumentException("You have already booked an appointment on this date");
        }
        Account doctor = accountService.resolveDoctor(appointmentRequest.getDoctorId(), workDate, desired);

        Shift shift = shiftService.findMatchingShift(desired);
        shiftService.checkTimeConflict(doctor.getId(), workDate, desired);

        Schedule schedule = scheduleService.findSchedule(doctor.getId(), workDate, shift.getId().intValue());
        Appointment appointment = buildAppointment(appointmentRequest, bookBy, doctor, schedule);

        // tạo reminder
        reminderService.createRemindersForAppointment(appointment);
        // gửi mail
        EmailDetailReminder emailDetailReminder = reminderService.buildEmailDetail(appointment);
        emailService.sendAppointmentSuccess(emailDetailReminder);
        return appointmentRepository.save(appointment);
    }

    public Appointment buildAppointment(AppointmentRequest appointmentRequest, Account user, Account doctor, Schedule schedule) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setUser(user);
        appointment.setTime(appointmentRequest.getDate());
        appointment.setStartTime(appointmentRequest.getTime());
        appointment.setEndTime(appointmentRequest.getTime().plusMinutes(30));
        appointment.setStatus(AppointmentStatus.UNPAID);
        appointment.setPatientName(appointmentRequest.getPatientName());
        appointment.setCreateAt(LocalDateTime.now());
        appointment.setPhoneNumber(appointmentRequest.getPhoneNumber());
        appointment.setMessage(appointmentRequest.getMessage());
        appointment.setSchedule(schedule);
        return appointment;
    }

    public boolean hasAppointmentToday(Long id, LocalDate date) {
        return appointmentRepository.existsByUserIdAndTimeAndStatusIsNot(id, date, AppointmentStatus.NOT_PAID);
    }

    public boolean updateAppointment(HttpServletRequest request, LocalDate date) {
        Account account = jwtService.extractAccount(jwtFilter.getToken(request));
        Appointment appointment = appointmentRepository.findByUserIdAndTime(account.getId(), date)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.UNCHECKED_IN);
        appointmentRepository.save(appointment);
        return true;
    }

    public List<Appointment> getAppointmentsByDoctorId(@Valid Authentication authentication) {
        Account account = jwtService.extractAccount(authentication.getName());
        List<Appointment> appointments = appointmentRepository.findByDoctorId(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        return appointments;
    }

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

    public AppointmentResponse getLastAppointment(Authentication authentication) {
        AppointmentResponse appointmentResponse;
        Account account = accountRepository.findByEmail(authentication.getName());
        Appointment appointment = appointmentRepository.findTopByUserOrderByCreateAtDesc(account);
        if(appointment == null) {
            appointmentResponse = null;
        } else {
            appointmentResponse = new AppointmentResponse(appointment);
        }
        return appointmentResponse;
    }
}
