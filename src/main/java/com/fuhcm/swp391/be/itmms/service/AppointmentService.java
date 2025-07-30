package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.JWTFilter;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AccountReportResponse;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentReportResponse;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetailReminder;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.InvoiceRepository;
import com.fuhcm.swp391.be.itmms.validation.Validation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentService paymentService;

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
        appointment = appointmentRepository.save(appointment);
        // tạo reminder
        reminderService.createReminders(appointment);
        // gửi mail sau khi booking thành công
        Invoice invoice = invoiceRepository.findByOwner(bookBy);
        String link = paymentService.createPaymentLink(invoice.getId());
        appointment.setNote(link);
        EmailDetailReminder emailDetailReminder = reminderService.buildEmailDetail(appointment);
        emailService.sendAppointmentSuccess(emailDetailReminder);
        // gửi noti khi booking thành công
        notificationService.notifyUser(appointment.getUser(), "Bạn đã đặt lịch khám bệnh thành công");
        return appointment;
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

    public List<AppointmentReportResponse> getAppointmentReport(@Valid @NotNull LocalDate fromDate,
                                                                @Valid @NotNull LocalDate toDate) {
        if(fromDate.isAfter(toDate)){
            throw new IllegalArgumentException("fromDate is after toDate");
        }
        List<AppointmentReportResponse> responses = new ArrayList<>();
        List<Appointment> appointments = appointmentRepository.findByTimeBetween(fromDate, toDate);
        System.out.println(appointments.size());
        LocalDate current = fromDate;
        while(!current.isAfter(toDate)){
            List<Appointment> unPaid = new ArrayList<>();
            List<Appointment> notPaid = new ArrayList<>();
            List<Appointment> unCheckin  = new ArrayList<>();
            List<Appointment> checkin = new ArrayList<>();
            List<Appointment> cancelled = new ArrayList<>();
            for(Appointment appointment : appointments){
                if(appointment.getTime().equals(current)){
                    if(appointment.getStatus().name().equalsIgnoreCase(AppointmentStatus.UNPAID.name())){
                        unPaid.add(appointment);
                    } else if(appointment.getStatus().name().equalsIgnoreCase(AppointmentStatus.NOT_PAID.name())){
                        notPaid.add(appointment);
                    } else if(appointment.getStatus().name().equalsIgnoreCase(AppointmentStatus.UNCHECKED_IN.name())){
                        unCheckin.add(appointment);
                    } else if(appointment.getStatus().name().equalsIgnoreCase(AppointmentStatus.CHECKED_IN.name())){
                        checkin.add(appointment);
                    } else if(appointment.getStatus().name().equalsIgnoreCase(AppointmentStatus.CANCELLED.name())){
                        cancelled.add(appointment);
                    }
                }
            }
            AppointmentReportResponse response = new AppointmentReportResponse();
            response.setDate(current);
            response.setUnPaid(unPaid.size());
            response.setNotPaid(notPaid.size());
            response.setUnCheckin(unCheckin.size());
            response.setCheckin(checkin.size());
            response.setCancelled(cancelled.size());
            response.setTotal(unPaid.size() + notPaid.size() + checkin.size() + cancelled.size());
            responses.add(response);
            current = current.plusDays(1);
        }
        return responses;
    }


    public List<AppointmentResponse> getListAppointmentsForReport(@Valid @NotNull LocalDate fromDate, @Valid @NotNull LocalDate toDate) {
        if(fromDate.isAfter(toDate)){
            throw new IllegalArgumentException("fromDate is after toDate");
        }
        List<Appointment> appointments = appointmentRepository.findByTimeBetween(fromDate, toDate);
        List<AppointmentResponse> responses = new ArrayList<>();
        for(Appointment appointment : appointments){
            responses.add(new AppointmentResponse(appointment));
        }
        return responses;
    }

    public AppointmentResponse updateAppointmentStatus(@Valid @NotNull Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment == null){
            throw new IllegalArgumentException("appointment not found");
        }
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);
        return new AppointmentResponse(appointment);
    }

    public List<AppointmentResponse> searchAppointments(String keyword, LocalDate date, Long doctorId) throws NotFoundException {
        List<Appointment> appointments = appointmentRepository.searchByKeywordDateAndDoctor(keyword, date, doctorId);
        if (appointments.isEmpty()) {
            throw new NotFoundException("Không có cuộc hẹn nào");
        }
        return appointments.stream()
                .map(appointment -> {
                    AppointmentResponse response = modelMapper.map(appointment, AppointmentResponse.class);
                    response.setPatientName(appointment.getUser() != null ? appointment.getUser().getFullName() : null);
                    response.setPhoneNumber(appointment.getUser() != null ? appointment.getUser().getPhoneNumber() : null);
                    response.setDoctorName(appointment.getDoctor().getFullName() + " - " + appointment.getDoctor().getDoctor().getPosition());
                    response.setDob(appointment.getUser().getUser().getDob());
                    response.setGender(appointment.getUser().getGender().name());
                    response.setUserId(appointment.getUser().getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsBookedByUser() throws NotFoundException {
        Long userId = authenticationService.getCurrentAccount().getId();
        List<Appointment> appointments = appointmentRepository.findByUserIdOrderByTimeDescStartTimeDesc(userId);
        if (appointments.isEmpty()) {
            throw new NotFoundException("Bạn chưa có cuộc hẹn nào");
        }
        return appointments.stream()
                .map(appointment -> {
                    AppointmentResponse response = modelMapper.map(appointment, AppointmentResponse.class);
                    response.setDoctorName(appointment.getDoctor().getFullName() + " - " + appointment.getDoctor().getDoctor().getPosition());
                    return response;
                })
                .collect(Collectors.toList());
    }




}
