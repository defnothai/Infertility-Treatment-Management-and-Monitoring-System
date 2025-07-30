package com.fuhcm.swp391.be.itmms.components;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.ReminderType;
import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.Reminder;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import com.fuhcm.swp391.be.itmms.repository.ReminderRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import com.fuhcm.swp391.be.itmms.service.EmailService;
import com.fuhcm.swp391.be.itmms.service.NotificationService;
import com.fuhcm.swp391.be.itmms.service.ReminderService;
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
    @Autowired
    private TreatmentSessionRepository treatmentSessionRepository;
    @Autowired
    private ReminderRepository reminderRepository;
    @Autowired
    private ReminderService reminderService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

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
                if (appointment.getSession() != null) {
                    TreatmentSession session = appointment.getSession();
                    session.setNotes("Bệnh nhân không đến khám");
                    session.setStatus(TreatmentSessionStatus.MISSED);
                    treatmentSessionRepository.save(session);
                    MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
                    medicalRecord.setNumberOfMissed(medicalRecord.getNumberOfMissed() + 1);
                    medicalRecordRepository.save(medicalRecord);
                }
            }
        }
        appointmentRepository.saveAll(appointmentsToCancel);
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void sendReminders() {
        List<Reminder> due = reminderRepository.findByRemindAtBeforeAndIsSentFalse(LocalDateTime.now());
        for (Reminder r : due) {
            Account user = r.getAppointment().getUser();
            if (r.getType() == ReminderType.EMAIL) {
                reminderService.handleEmailReminder(r);
            }
            if (r.getType() == ReminderType.WEB_NOTIFICATION) {
                notificationService.notifyUser(user, r.getContent());
            }
            r.setSent(true);
            r.setSentAt(LocalDateTime.now());
            reminderRepository.save(r);
        }
    }


}
