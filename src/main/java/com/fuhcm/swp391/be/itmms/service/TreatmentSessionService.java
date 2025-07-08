package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentSessionRequest;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentSessionResponse;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreatmentSessionService {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final ModelMapper modelMapper;
    private final TreatmentStageProgressRepository progressRepository;
    private final TreatmentSessionRepository sessionRepository;

    public TreatmentSessionService(TreatmentSessionRepository treatmentSessionRepository,
                                   ModelMapper modelMapper,
                                   TreatmentStageProgressRepository progressRepository,
                                   TreatmentSessionRepository sessionRepository) {
        this.treatmentSessionRepository = treatmentSessionRepository;
        this.modelMapper = modelMapper;
        this.progressRepository = progressRepository;
        this.sessionRepository = sessionRepository;
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
        TreatmentSession session = modelMapper.map(request, TreatmentSession.class);
        session.setProgress(progress);
        session.setActive(true);
        TreatmentSession saved = sessionRepository.save(session);
        return modelMapper.map(saved, TreatmentSessionResponse.class);
    }

    public void softDeleteById(Long sessionId) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));

        session.setActive(false);
        sessionRepository.save(session);
    }

    public TreatmentSessionResponse update(Long sessionId, TreatmentSessionRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Buổi khám không tồn tại"));
        session.setDate(request.getDate());
        session.setDiagnosis(request.getDiagnosis());
        session.setSymptoms(request.getSymptoms());
        session.setNotes(request.getNotes());
        TreatmentSession updated = sessionRepository.save(session);
        return modelMapper.map(updated, TreatmentSessionResponse.class);
    }



}
