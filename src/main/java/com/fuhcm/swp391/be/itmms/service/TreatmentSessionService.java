package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import com.fuhcm.swp391.be.itmms.dto.request.FollowUpRequest;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentSessionRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.dto.response.SessionDetailsResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentSessionResponse;
import com.fuhcm.swp391.be.itmms.dto.response.UltrasoundResponse;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final ScheduleRepository scheduleRepository;
    private final AuthenticationService authenticationService;
    private final AppointmentRepository appointmentRepository;
    private final ShiftService shiftService;
    private final ScheduleService scheduleService;

    public List<TreatmentSessionResponse> getByProgressId(Long progressId) throws NotFoundException {
        List<TreatmentSession> sessions = treatmentSessionRepository.findByProgress_IdAndIsActive(progressId, true);
        if (sessions.isEmpty()) {
            throw new NotFoundException("Chưa có buổi khám nào trong giai đoạn này");
        }
        return sessions.stream()
                .map(session -> modelMapper.map(session, TreatmentSessionResponse.class))
                .collect(Collectors.toList());
    }

    public TreatmentSessionResponse createFollowUpSession(Long progressId,
                                                   FollowUpRequest request)
                    throws NotFoundException {
        TreatmentStageProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giai đoạn của phác đồ"));
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        // session
        TreatmentSession session = new TreatmentSession();
        session.setDiagnosis("");
        session.setDate(request.getDate());
        session.setProgress(progress);
        session.setActive(true);
        session.setStatus(TreatmentSessionStatus.PENDING);
        TreatmentSession saved = sessionRepository.save(session);

        // appointment
        Account doctor = authenticationService.getCurrentAccount();
        Account user = medicalRecord.getUser().getAccount();
        Shift shift = shiftService.findMatchingShift(request.getTime());
        Schedule schedule = scheduleService.findSchedule(doctor.getId(), request.getDate(), shift.getId().intValue());
        Appointment appointment = buildAppointment(request, user, doctor, schedule, saved);
        appointmentRepository.save(appointment);
        return modelMapper.map(saved, TreatmentSessionResponse.class);
    }

    public Appointment buildAppointment(FollowUpRequest request,
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
        return appointment;
    }

    public TreatmentSessionResponse updateFollowUpSession(Long sessionId, FollowUpRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy buổi khám"));

        if (!session.isActive()) {
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

        Account doctor = authenticationService.getCurrentAccount();
        Shift shift = shiftService.findMatchingShift(request.getTime());
        Schedule schedule = scheduleService.findSchedule(doctor.getId(), request.getDate(), shift.getId().intValue());

        appointment.setTime(request.getDate());
        appointment.setStartTime(request.getTime());
        appointment.setEndTime(request.getTime().plusMinutes(30));
        appointment.setSchedule(schedule);
        appointment.setMessage(request.getMessage());
        appointmentRepository.save(appointment);
        return modelMapper.map(updated, TreatmentSessionResponse.class);
    }

    public void deleteFollowUpSession(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy buổi khám"));

        Appointment appointment = appointmentRepository.findBySessionId(sessionId)
                .orElse(null);
        if (appointment != null) {
            session.setActive(false);
            appointmentRepository.delete(appointment);
        }
    }



    public void softDeleteById(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canDelete(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }

        session.setActive(false);
        sessionRepository.save(session);
    }

    public TreatmentSessionResponse update(Long sessionId, TreatmentSessionRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        session.setDate(request.getDate());
        session.setDiagnosis(request.getDiagnosis());
        session.setSymptoms(request.getSymptoms());
        session.setNotes(request.getNotes());
        TreatmentSession updated = sessionRepository.save(session);
        return modelMapper.map(updated, TreatmentSessionResponse.class);
    }

    public SessionDetailsResponse getSessionDetail(Long sessionId) throws NotFoundException {
        TreatmentSession session = treatmentSessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin buổi khám"));

        SessionDetailsResponse response = new SessionDetailsResponse();

        List<LabTestResultResponse> labResponses = session.getLabTestResults().stream()
                .map(lab -> {
                    LabTestResultResponse dto = new LabTestResultResponse();
                    dto.setId(lab.getId());
                    dto.setTestDate(lab.getTestDate());
                    dto.setResultSummary(lab.getResultSummary());
                    dto.setResultDetails(lab.getResultDetails());
                    dto.setStatus(lab.getStatus());
                    dto.setNotes(lab.getNotes());
                    dto.setLabTestName(lab.getTest().getName());
                    dto.setStaffFullName(lab.getAccount().getFullName());
                    return dto;
                }).toList();
        response.setLabTestResults(labResponses);

        List<UltrasoundResponse> usResponses = session.getUltrasounds().stream()
                .filter(Ultrasound::isActive)
                .map(us -> {
                    UltrasoundResponse dto = new UltrasoundResponse();
                    dto.setId(us.getId());
                    dto.setDate(us.getDate());
                    dto.setResult(us.getResult());
                    dto.setImgUrls(List.of(us.getImageUrls().split(";")));
                    return dto;
                }).toList();
        response.setUltrasounds(usResponses);

        return response;
    }


    public FollowUpRequest getFollowUpDetail(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        Appointment appointment = appointmentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundException("Chưa đặt hẹn lịch tái khám"));
        return new FollowUpRequest(appointment.getTime(), appointment.getStartTime(), appointment.getMessage());
    }
}
