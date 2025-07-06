package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanRequest;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentPlanResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentPlanRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentStageProgressRepository treatmentStageProgressRepository;
    private final ModelMapper modelMapper;
    private final ServiceService serviceService;
    private final MedicalRecordService medicalRecordService;

    public TreatmentPlanService(TreatmentPlanRepository treatmentPlanRepository,
                                TreatmentStageProgressRepository treatmentStageProgressRepository,
                                ModelMapper modelMapper,
                                ServiceService serviceService,
                                MedicalRecordService medicalRecordService) {
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.treatmentStageProgressRepository = treatmentStageProgressRepository;
        this.modelMapper = modelMapper;
        this.serviceService = serviceService;
        this.medicalRecordService = medicalRecordService;
    }

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
        com.fuhcm.swp391.be.itmms.entity.service.Service service = serviceService.findById(request.getServiceId());

        TreatmentPlan plan = new TreatmentPlan();
        plan.setMedicalRecord(medicalRecord);
        plan.setService(service);
        plan.setDayStart(LocalDate.now());

        // Lưu plan trước để có id (vì progress cần foreign key)
        TreatmentPlan savedPlan = treatmentPlanRepository.save(plan);

        // Tạo progress và gán foreign key
        List<TreatmentStageProgress> progresses = new ArrayList<>();
        for (ServiceStage stage : service.getStage()) {
            TreatmentStageProgress progress = new TreatmentStageProgress();
            progress.setPlan(savedPlan); // gán plan đã có id
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
    public TreatmentPlanResponse updateTreatmentPlan(Long planId, TreatmentPlanRequest request) throws NotFoundException {
        TreatmentPlan existingPlan = treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Bạn chưa tạo phác đồ điều trị trước đó"));

        // Đổi service
        com.fuhcm.swp391.be.itmms.entity.service.Service newService = serviceService.findById(request.getServiceId());
        existingPlan.setService(newService);

        // Đánh dấu các progress cũ là inactive
        for (TreatmentStageProgress progress : existingPlan.getTreatmentStageProgress()) {
            progress.setActive(false);
        }

        // Tạo mới các progress
        List<TreatmentStageProgress> newProgresses = new ArrayList<>();
        for (ServiceStage stage : newService.getStage()) {
            TreatmentStageProgress progress = new TreatmentStageProgress();
            progress.setPlan(existingPlan);
            progress.setServiceStage(stage);
            progress.setStatus(TreatmentStageStatus.NOT_STARTED);
            progress.setActive(true);
            progress.setNotes("");
            newProgresses.add(progress);
        }

        // Lưu các progress mới một cách chủ động (thay vì rely vào cascade)
        List<TreatmentStageProgress> savedProgresses = treatmentStageProgressRepository.saveAll(newProgresses);

        // Thêm vào list plan
        existingPlan.getTreatmentStageProgress().addAll(savedProgresses);

        // Lưu lại plan (chỉ cập nhật service, không rely vào cascade để insert progress)
        treatmentPlanRepository.save(existingPlan);

        return buildTreatmentPlanResponse(existingPlan, savedProgresses);
    }


    private TreatmentPlanResponse buildTreatmentPlanResponse(TreatmentPlan plan, List<TreatmentStageProgress> progresses) {
        TreatmentPlanResponse response = modelMapper.map(plan, TreatmentPlanResponse.class);
        response.setServiceName(plan.getService().getServiceName());

        List<TreatmentStageProgressResponse> progressResponses = new ArrayList<>();
        for (TreatmentStageProgress progress : progresses) {
            progressResponses.add(toStageProgressResponse(progress));
        }

        response.setTreatmentStageProgressResponses(progressResponses);
        response.setDateStart(plan.getDayStart());

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
