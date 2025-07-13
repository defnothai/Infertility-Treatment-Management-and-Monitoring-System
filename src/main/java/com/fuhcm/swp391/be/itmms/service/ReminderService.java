//package com.fuhcm.swp391.be.itmms.service;
//
//import com.fuhcm.swp391.be.itmms.constant.ReminderType;
//import com.fuhcm.swp391.be.itmms.entity.Appointment;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ReminderService {
//
//    private void createReminderIfNotExists(Appointment appointment, int hoursBefore, ReminderType type, String message) {
//        if (reminderRepository.existsByAppointmentAndReminderType(appointment, type)) return;
//
//        LocalDateTime appointmentDateTime = appointment.getTime().atTime(appointment.getStartTime());
//        LocalDateTime remindDateTime = appointmentDateTime.minusHours(hoursBefore);
//
//        Reminder reminder = new Reminder();
//        reminder.setTitle("Nhắc lịch hẹn");
//        reminder.setDescription(message);
//        reminder.setReminderType(type);
//        reminder.setRemindDate(remindDateTime);
//        reminder.setAppointment(appointment);
//        reminder.setUser(appointment.getUser());
//        reminder.setIsSent(false);
//
//        reminderRepository.save(reminder);
//    }
//
//
//
//}
