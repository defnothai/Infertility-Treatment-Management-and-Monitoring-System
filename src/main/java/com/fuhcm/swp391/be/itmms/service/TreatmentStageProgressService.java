package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentStageProgressRequest;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TreatmentStageProgressService {

    private final TreatmentStageProgressRepository treatmentStageProgressRepository;
    private final ModelMapper modelMapper;
    private final MedicalRecordAccessService medicalRecordAccessService;

    public TreatmentStageProgressService(TreatmentStageProgressRepository treatmentStageProgressRepository, ModelMapper modelMapper, MedicalRecordAccessService medicalRecordAccessService) {
        this.treatmentStageProgressRepository = treatmentStageProgressRepository;
        this.modelMapper = modelMapper;
        this.medicalRecordAccessService = medicalRecordAccessService;
    }

    public TreatmentStageProgressResponse update(Long id, TreatmentStageProgressRequest request) throws NotFoundException {
        TreatmentStageProgress progress = treatmentStageProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giai đoạn của phác đồ không tồn tại"));
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        progress.setDayStart(request.getDateStart());
        progress.setDayComplete(request.getDateComplete());
        progress.setNotes(request.getNotes());
        progress.setStatus(request.getStatus());

        TreatmentStageProgress saved = treatmentStageProgressRepository.save(progress);

        TreatmentStageProgressResponse response = modelMapper.map(saved, TreatmentStageProgressResponse.class);
        if (saved.getServiceStage() != null) {
            response.setStageName(saved.getServiceStage().getName());
        }
        return response;
    }
}

