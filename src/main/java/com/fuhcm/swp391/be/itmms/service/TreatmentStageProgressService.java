package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentStageProgressRequest;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TreatmentStageProgressService {

    private final TreatmentStageProgressRepository treatmentStageProgressRepository;
    private final ModelMapper modelMapper;

    public TreatmentStageProgressService(TreatmentStageProgressRepository treatmentStageProgressRepository, ModelMapper modelMapper) {
        this.treatmentStageProgressRepository = treatmentStageProgressRepository;
        this.modelMapper = modelMapper;
    }

    public TreatmentStageProgressResponse update(Long id, TreatmentStageProgressRequest request) throws NotFoundException {
        TreatmentStageProgress progress = treatmentStageProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giai đoạn của phác đồ không tồn tại"));

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

