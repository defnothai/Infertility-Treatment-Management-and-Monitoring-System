package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ReminderType;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetailReminder;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Reminder;
import com.fuhcm.swp391.be.itmms.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final EmailService emailService;

    public void createRemindersForAppointment(Appointment appointment) {
        createReminderIfNotExists(appointment, 24, ReminderType.APPOINTMENT_ONE_DAY_BEFORE, "Bạn có lịch hẹn vào ngày mai.");
        createReminderIfNotExists(appointment, 2, ReminderType.APPOINTMENT_FIVE_HOURS_BEFORE, "Bạn có lịch hẹn sau 5 tiếng nữa.");
    }

    public void createReminderIfNotExists(Appointment appointment, int hoursBefore, ReminderType type, String message) {
        if (reminderRepository.existsByAppointmentAndReminderType(appointment, type)) return;

        LocalDateTime appointmentDateTime = appointment.getTime().atTime(appointment.getStartTime());
        LocalDateTime remindDateTime = appointmentDateTime.minusHours(hoursBefore);

        Reminder reminder = new Reminder();
        reminder.setTitle("NHẮC LỊCH HẸN");
        reminder.setDescription(message);
        reminder.setReminderType(type);
        reminder.setRemindDate(remindDateTime);
        reminder.setAppointment(appointment);
        reminder.setUser(appointment.getUser().getUser());
        reminder.setSent(false);
        reminderRepository.save(reminder);
    }

    public void handleReminder(Reminder reminder) {
        if (reminder.getUser() == null || reminder.getUser().getAccount() == null) return;

        Account user = reminder.getUser().getAccount();
        if (user.getEmail() == null || user.getEmail().isBlank()) return;

        EmailDetailReminder emailDetail = buildEmailDetail(reminder.getAppointment());
        emailService.sendReminderEmail(emailDetail);
        reminder.setSent(true);
        reminderRepository.save(reminder);
    }

    public EmailDetailReminder buildEmailDetail(Appointment appointment) {
        EmailDetailReminder emailDetail = new EmailDetailReminder();
        emailDetail.setRecipient(appointment.getUser().getEmail());
        emailDetail.setSubject("NHẮC NHỞ LỊCH HẸN");
        emailDetail.setPatientName(appointment.getUser().getFullName());
        emailDetail.setAppointmentDate(appointment.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        emailDetail.setStartTime(appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setEndTime(appointment.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setDoctorName(appointment.getDoctor().getFullName());
        emailDetail.setNote(appointment.getNote());
        emailDetail.setMessage(appointment.getMessage());
        return emailDetail;
    }








}
