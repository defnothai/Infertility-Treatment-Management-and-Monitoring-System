package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanRequest;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentPlanResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentPlanRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentStageProgressRepository treatmentStageProgressRepository;
    private final ModelMapper modelMapper;
    private final ServiceService serviceService;
    private final MedicalRecordService medicalRecordService;
    private final MedicalRecordAccessService medicalRecordAccessService;

    public List<TreatmentPlanResponse> getByMedicalRecordId(Long medicalRecordId) {
        List<TreatmentPlan> plans = treatmentPlanRepository.findByMedicalRecord_Id(medicalRecordId);
        List<TreatmentPlanResponse> responses = new ArrayList<>();

        for (TreatmentPlan plan : plans) {
            TreatmentPlanResponse response = modelMapper.map(plan, TreatmentPlanResponse.class);
            response.setServiceName(plan.getService().getServiceName());

            List<TreatmentStageProgressResponse> stageResponses = new ArrayList<>();
            for (TreatmentStageProgress progress : plan.getTreatmentStageProgress()) {
                if (progress.isActive()) {
                    stageResponses.add(toStageProgressResponse(progress));
                }
            }

            response.setTreatmentStageProgressResponses(stageResponses);
            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public TreatmentPlanResponse createTreatmentPlanWithStages(TreatmentPlanRequest request) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordService.findById(request.getMedicalRecordId());
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        com.fuhcm.swp391.be.itmms.entity.service.Service service = serviceService.findById(request.getServiceId());

        TreatmentPlan plan = new TreatmentPlan();
        plan.setMedicalRecord(medicalRecord);
        plan.setService(service);
        plan.setDayStart(LocalDate.now());
        plan.setStatus(TreatmentPlanStatus.IN_PROGRESS);

        TreatmentPlan savedPlan = treatmentPlanRepository.save(plan);

        List<TreatmentStageProgress> progresses = new ArrayList<>();
        for (ServiceStage stage : service.getStage()) {
            TreatmentStageProgress progress = new TreatmentStageProgress();
            progress.setPlan(savedPlan);
            progress.setServiceStage(stage);
            progress.setStatus(TreatmentStageStatus.NOT_STARTED);
            progress.setActive(true);
            progress.setNotes("");
            progresses.add(progress);
        }

        List<TreatmentStageProgress> savedProgresses = treatmentStageProgressRepository.saveAll(progresses);
        savedPlan.setTreatmentStageProgress(savedProgresses);

        return buildTreatmentPlanResponse(savedPlan, savedProgresses);
    }


    @Transactional
    public TreatmentPlanResponse updateTreatmentPlan(Long planId, TreatmentPlanUpdateRequest request) throws NotFoundException {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Bạn chưa tạo phác đồ điều trị trước đó"));
        MedicalRecord medicalRecord = plan.getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không có quyền sử dụng tính năng này");
        }
        plan.setDayEnd(LocalDate.now());
        plan.setStatus(request.getStatus());
        plan.setNotes(request.getNotes());
        TreatmentPlan savedPlan = treatmentPlanRepository.save(plan);
        return buildTreatmentPlanResponse(savedPlan, savedPlan.getTreatmentStageProgress());

    }

    private TreatmentPlanResponse buildTreatmentPlanResponse(TreatmentPlan plan, List<TreatmentStageProgress> progresses) {
        TreatmentPlanResponse response = modelMapper.map(plan, TreatmentPlanResponse.class);
        response.setServiceName(plan.getService().getServiceName());

        List<TreatmentStageProgressResponse> progressResponses = new ArrayList<>();
        for (TreatmentStageProgress progress : progresses) {
            progressResponses.add(toStageProgressResponse(progress));
        }

        response.setTreatmentStageProgressResponses(progressResponses);
        response.setDayStart(plan.getDayStart());

        return response;
    }

    private TreatmentStageProgressResponse toStageProgressResponse(TreatmentStageProgress progress) {
        TreatmentStageProgressResponse res = new TreatmentStageProgressResponse();
        res.setId(progress.getId());
        res.setDateStart(progress.getDayStart());
        res.setDateComplete(progress.getDayComplete());
        res.setNotes(progress.getNotes());
        res.setStatus(progress.getStatus());
        res.setStageName(progress.getServiceStage().getName());
        return res;
    }

}
