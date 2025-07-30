package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ReminderType;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetail;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetailReminder;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Reminder;
import com.fuhcm.swp391.be.itmms.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final EmailService emailService;

    public void createReminders(Appointment a) {
        LocalDateTime start = LocalDateTime.of(a.getTime(), a.getStartTime());

        reminderRepository.saveAll(List.of(
                new Reminder("NHẮC NHỞ LỊCH KHÁM 1 NGÀY NỮA", start.minusDays(1), ReminderType.EMAIL, a),
                new Reminder("Nhắc lịch khám 1 ngày nữa", start.minusDays(1), ReminderType.WEB_NOTIFICATION, a),
                new Reminder("NHẮC NHỞ LỊCH KHÁM SAU 5 TIẾNG NỮA", start.minusHours(5), ReminderType.EMAIL, a),
                new Reminder("Nhắc lịch khám sau 5 tiếng", start.minusHours(5), ReminderType.WEB_NOTIFICATION, a)
        ));
    }

    public void handleEmailReminder(Reminder reminder) {
        EmailDetailReminder emailDetail = buildEmailDetail(reminder.getAppointment());
        emailService.sendReminderEmail(emailDetail);
    }

    public EmailDetailReminder buildEmailDetail(Appointment appointment) {
        EmailDetailReminder emailDetail = new EmailDetailReminder();
        emailDetail.setRecipient(appointment.getUser().getEmail());
        emailDetail.setSubject("THÔNG BÁO LỊCH HẸN CỦA BẠN");
        emailDetail.setPatientName(appointment.getUser().getFullName());
        emailDetail.setAppointmentDate(appointment.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        emailDetail.setStartTime(appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setEndTime(appointment.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setDoctorName(appointment.getDoctor().getFullName());
        emailDetail.setNote(appointment.getNote());
        emailDetail.setMessage(appointment.getMessage());
        return emailDetail;
    }

    public EmailDetail buildEmailForPayment(String link, String email, String fullName, double amount) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(email);
        emailDetail.setSubject(link);
        emailDetail.setFullName(fullName);
        emailDetail.setLink(amount + "");
        return emailDetail;
    }

}
