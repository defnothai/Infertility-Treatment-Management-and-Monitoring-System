package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentSessionRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.dto.response.SessionDetailsResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentSessionResponse;
import com.fuhcm.swp391.be.itmms.dto.response.UltrasoundResponse;
import com.fuhcm.swp391.be.itmms.entity.Ultrasound;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreatmentSessionService {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final ModelMapper modelMapper;
    private final TreatmentStageProgressRepository progressRepository;
    private final TreatmentSessionRepository sessionRepository;
    private final MedicalRecordAccessService medicalRecordAccessService;

    public TreatmentSessionService(TreatmentSessionRepository treatmentSessionRepository,
                                   ModelMapper modelMapper,
                                   TreatmentStageProgressRepository progressRepository,
                                   TreatmentSessionRepository sessionRepository, MedicalRecordAccessService medicalRecordAccessService) {
        this.treatmentSessionRepository = treatmentSessionRepository;
        this.modelMapper = modelMapper;
        this.progressRepository = progressRepository;
        this.sessionRepository = sessionRepository;
        this.medicalRecordAccessService = medicalRecordAccessService;
    }

    public List<TreatmentSessionResponse> getByProgressId(Long progressId) throws NotFoundException {
        List<TreatmentSession> sessions = treatmentSessionRepository.findByProgress_IdAndIsActive(progressId, true);
        if (sessions.isEmpty()) {
            throw new NotFoundException("Chưa có buổi khám nào trong giai đoạn này");
        }
        return sessions.stream()
                .map(session -> modelMapper.map(session, TreatmentSessionResponse.class))
                .collect(Collectors.toList());
    }

    public TreatmentSessionResponse create(Long progressId, TreatmentSessionRequest request) throws NotFoundException {
        TreatmentStageProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giai đoạn của phác đồ"));
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        TreatmentSession session = modelMapper.map(request, TreatmentSession.class);
        session.setProgress(progress);
        session.setActive(true);
        TreatmentSession saved = sessionRepository.save(session);
        return modelMapper.map(saved, TreatmentSessionResponse.class);
    }

    public void softDeleteById(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canDelete(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }

        session.setActive(false);
        sessionRepository.save(session);
    }

    public TreatmentSessionResponse update(Long sessionId, TreatmentSessionRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
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
        TreatmentSession session = treatmentSessionRepository.findById(sessionId)
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




}
