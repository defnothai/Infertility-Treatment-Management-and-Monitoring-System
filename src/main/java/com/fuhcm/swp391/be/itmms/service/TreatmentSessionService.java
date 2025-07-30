package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import com.fuhcm.swp391.be.itmms.dto.request.FollowUpDTO;
import com.fuhcm.swp391.be.itmms.dto.request.FollowUpDeleteRequest;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentSessionRequest;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentSessionService {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final ModelMapper modelMapper;
    private final TreatmentStageProgressRepository progressRepository;
    private final TreatmentSessionRepository sessionRepository;
    private final MedicalRecordAccessService medicalRecordAccessService;
    private final AuthenticationService authenticationService;
    private final AppointmentRepository appointmentRepository;
    private final ShiftService shiftService;
    private final ScheduleService scheduleService;
    private final ReminderService reminderService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ReminderRepository reminderRepository;

    public List<TreatmentSessionResponse> getByProgressId(Long progressId) throws NotFoundException {
        List<TreatmentSession> sessions = treatmentSessionRepository.findByProgress_IdAndIsActive(progressId, true);
        if (sessions.isEmpty()) {
            throw new NotFoundException("Chưa có buổi khám nào trong giai đoạn này");
        }
        return sessions.stream()
                .map(session -> modelMapper.map(session, TreatmentSessionResponse.class))
                .collect(Collectors.toList());
    }

    // tạo lịch hẹn
    @Transactional
    public TreatmentSessionResponse createFollowUpSession(Long progressId,
                                                   FollowUpDTO request)
                    throws NotFoundException {
        TreatmentStageProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giai đoạn của phác đồ"));
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        TreatmentPlan plan = progress.getPlan();
        boolean hasUnfinishedSession = sessionRepository.existsByProgressPlanIdAndStatusAndIsActiveTrue(plan.getId(), TreatmentSessionStatus.PENDING);
        if (hasUnfinishedSession) {
            throw new RuntimeException("Bệnh nhân đã có lịch tái hẹn trong phác đồ điều trị.");
        }
        // session
        TreatmentSession session = new TreatmentSession();
        session.setDiagnosis("");
        session.setSymptoms("");
        session.setDate(request.getDate());
        session.setProgress(progress);
        session.setActive(true);
        session.setStatus(TreatmentSessionStatus.PENDING);
        TreatmentSession saved = sessionRepository.save(session);

        // stage
        if (progress.getStatus() == TreatmentStageStatus.NOT_STARTED) {
            progress.setStatus(TreatmentStageStatus.IN_PROGRESS);
            progressRepository.save(progress);
        }

        // appointment
        Account doctor = authenticationService.getCurrentAccount();
        Account user = medicalRecord.getUser().getAccount();
        Shift shift = shiftService.findMatchingShift(request.getTime());
        Schedule schedule = scheduleService.findSchedule(doctor.getId(), request.getDate(), shift.getId().intValue());
        Appointment appointment = buildAppointment(request, user, doctor, schedule, saved);
        appointmentRepository.save(appointment);

        // tạo reminder
        reminderService.createReminders(appointment);
        // gửi mail sau khi bác sĩ hẹn khám
        EmailDetailReminder emailDetailReminder = reminderService.buildEmailDetail(appointment);
        emailService.sendReminderEmail(emailDetailReminder);
        // gửi noti khi booking thành công
        notificationService.notifyUser(appointment.getUser(), "Bác sĩ đã đặt lịch hẹn khám cho bạn vào ngày " + appointment.getTime());
        return modelMapper.map(saved, TreatmentSessionResponse.class);
    }

    public Appointment buildAppointment(FollowUpDTO request,
                                        Account user,
                                        Account doctor,
                                        Schedule schedule,
                                        TreatmentSession session) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setUser(user);
        appointment.setTime(request.getDate());
        appointment.setStartTime(request.getTime());
        appointment.setEndTime(request.getTime().plusMinutes(30));
        appointment.setStatus(AppointmentStatus.UNCHECKED_IN);
        appointment.setPatientName(user.getFullName());
        appointment.setCreateAt(LocalDateTime.now());
        appointment.setPhoneNumber(user.getPhoneNumber());
        appointment.setMessage(request.getMessage());
        appointment.setSchedule(schedule);
        appointment.setSession(session);
        appointment.setNote(request.getAppointmentNote());
        return appointment;
    }

    // lấy chi tiết lịch đã hẹn
    public FollowUpDTO getFollowUpDetail(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        Appointment appointment = appointmentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundException("Chưa đặt hẹn lịch tái khám"));
        return new FollowUpDTO(appointment.getTime(), appointment.getStartTime(), appointment.getMessage(), appointment.getNote());
    }

    // update lịch hẹn
    public TreatmentSessionResponse updateFollowUpSession(Long sessionId, FollowUpDTO request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy buổi khám"));

        if (!session.isActive() || session.getStatus() != TreatmentSessionStatus.PENDING) {
            throw new IllegalStateException("Buổi khám đã bị hủy hoặc kết thúc, không thể chỉnh sửa");
        }

        TreatmentStageProgress progress = session.getProgress();
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();


        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể chỉnh sửa buổi khám này");
        }
        // session
        session.setDate(request.getDate());
        TreatmentSession updated = sessionRepository.save(session);
        // appointment
        Appointment appointment = appointmentRepository.findBySessionId(session.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy cuộc hẹn tương ứng"));

        LocalDate oldDate = appointment.getTime();
        LocalTime oldTime = appointment.getStartTime();

        //
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getTime(), appointment.getStartTime());
        Duration timeToAppointment = Duration.between(now, appointmentDateTime);

        if (!timeToAppointment.isNegative() && timeToAppointment.toMinutes() < 60 * 24) {
            throw new IllegalStateException("Không thể cập nhật cuộc hẹn");
        }

        Account doctor = authenticationService.getCurrentAccount();
        Shift shift = shiftService.findMatchingShift(request.getTime());
        Schedule schedule = scheduleService.findSchedule(doctor.getId(), request.getDate(), shift.getId().intValue());

        appointment.setTime(request.getDate());
        appointment.setStartTime(request.getTime());
        appointment.setEndTime(request.getTime().plusMinutes(30));
        appointment.setSchedule(schedule);
        appointment.setMessage(request.getMessage());
        appointment.setNote(request.getAppointmentNote());
        appointment = appointmentRepository.save(appointment);
        // remove reminder
        List<Reminder> reminders = reminderRepository.findByAppointment_Id(appointment.getId());
        if (!reminders.isEmpty()) {
            reminderRepository.deleteAll(reminders);
        }
        Account userAccount = medicalRecord.getUser().getAccount();
        // add reminder
        reminderService.createReminders(appointment);
        // gui mail thong bao
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setFullName(appointment.getUser().getFullName());
        emailDetail.setNote(appointment.getNote() != null ? appointment.getNote() : "");
        emailDetail.setMessage(appointment.getMessage() != null ? appointment.getMessage() : "");
        emailDetail.setOldDate(oldDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        emailDetail.setOldTime(oldTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setNewDate(appointment.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        emailDetail.setNewTime(appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        emailDetail.setDoctorName(appointment.getDoctor().getFullName());
        emailDetail.setRecipient(userAccount.getEmail());
        emailDetail.setSubject("CẬP NHẬT CUỘC HẸN BUỔI KHÁM");
        emailService.sendUpdateAppointment(emailDetail);
        // gui noti thong bao
        notificationService.notifyUser(userAccount, "Bác sĩ đã cập nhật lịch hẹn của bạn sang ngày " + appointment.getTime() + " lúc " + appointment.getStartTime());
        return modelMapper.map(updated, TreatmentSessionResponse.class);
    }


    public TreatmentSessionResponse update(Long sessionId, TreatmentSessionRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        session.setDiagnosis(request.getDiagnosis());
        session.setSymptoms(request.getSymptoms());
        session.setNotes(request.getNotes());
        session.setStatus(TreatmentSessionStatus.COMPLETED);
        TreatmentSession updated = sessionRepository.save(session);
        return modelMapper.map(updated, TreatmentSessionResponse.class);
    }

    public TreatmentSessionResponse getSessionDetail(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy buổi khám"));
        return modelMapper.map(session, TreatmentSessionResponse.class);
    }


//    public SessionDetailsResponse getSessionDetail(Long sessionId) throws NotFoundException {
//        TreatmentSession session = treatmentSessionRepository.findByIdAndIsActiveTrue(sessionId)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin buổi khám"));
//
//        SessionDetailsResponse response = new SessionDetailsResponse();
//
//        List<LabTestResultResponse> labResponses = session.getLabTestResults().stream()
//                .map(lab -> {
//                    LabTestResultResponse dto = new LabTestResultResponse();
//                    dto.setId(lab.getId());
//                    dto.setTestDate(lab.getTestDate());
//                    dto.setResultSummary(lab.getResultSummary());
//                    dto.setResultDetails(lab.getResultDetails());
//                    dto.setStatus(lab.getStatus());
//                    dto.setNotes(lab.getNotes());
//                    dto.setLabTestName(lab.getTest().getName());
//                    dto.setStaffFullName(lab.getAccount().getFullName());
//                    return dto;
//                }).toList();
//        response.setLabTestResults(labResponses);
//
//        List<UltrasoundResponse> usResponses = session.getUltrasounds().stream()
//                .filter(Ultrasound::isActive)
//                .map(us -> {
//                    UltrasoundResponse dto = new UltrasoundResponse();
//                    dto.setId(us.getId());
//                    dto.setDate(us.getDate());
//                    dto.setResult(us.getResult());
//                    dto.setImgUrls(List.of(us.getImageUrls().split(";")));
//                    return dto;
//                }).toList();
//        response.setUltrasounds(usResponses);
//
//        return response;
//    }

    //    public void softDeleteById(Long sessionId) throws NotFoundException {
//        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
//                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
//        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
//        if (medicalRecord != null && !medicalRecordAccessService.canDelete(medicalRecord)) {
//            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
//        }
//
//        session.setActive(false);
//        sessionRepository.save(session);
//    }

    //
//    public void deleteFollowUpSession(Long sessionId, FollowUpDeleteRequest request) throws NotFoundException {
//        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy buổi khám"));
//
//        Appointment appointment = appointmentRepository.findBySessionId(sessionId)
//                .orElse(null);
//        if (appointment != null) {
//            session.setActive(false);
//            appointment.setStatus(AppointmentStatus.CANCELLED);
//            appointment.setNote(request.getNote());
//            appointment.setMessage(request.getMessage());
//        }
//    }



}
