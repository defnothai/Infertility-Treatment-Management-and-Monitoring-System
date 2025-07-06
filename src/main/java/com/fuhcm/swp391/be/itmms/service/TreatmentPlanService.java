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
    private final ModelMapper modelMapper;
    private final ServiceService serviceService;
    private final MedicalRecordService medicalRecordService;

    public TreatmentPlanService(TreatmentPlanRepository treatmentPlanRepository,
                                ModelMapper modelMapper,
                                ServiceService serviceService,
                                MedicalRecordService medicalRecordService,
                                TreatmentStageProgressRepository treatmentStageProgressRepository) {
        this.treatmentPlanRepository = treatmentPlanRepository;
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
                TreatmentStageProgressResponse progressResponse = modelMapper.map(progress, TreatmentStageProgressResponse.class);
                progressResponse.setStageName(progress.getServiceStage().getName());
                stageResponses.add(progressResponse);
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
        TreatmentPlan plan = buildTreatmentPlan(request, medicalRecord, service);

        List<TreatmentStageProgress> progresses = buildStageProgresses(plan, service.getStage());
        plan.setTreatmentStageProgress(progresses);

        // Lưu TreatmentPlan (và cascade cả progresses)
        treatmentPlanRepository.save(plan);
        return buildTreatmentPlanResponse(plan, progresses, service);
    }

    private TreatmentPlan buildTreatmentPlan(TreatmentPlanRequest request, MedicalRecord record,
                                             com.fuhcm.swp391.be.itmms.entity.service.Service service) {
        TreatmentPlan plan = new TreatmentPlan();
        plan.setMedicalRecord(record);
        plan.setService(service);
        plan.setDayStart(LocalDate.now());
        return plan;
    }

    private List<TreatmentStageProgress> buildStageProgresses(TreatmentPlan plan, List<ServiceStage> serviceStages) {
        List<TreatmentStageProgress> progresses = new ArrayList<>();
        for (ServiceStage stage : serviceStages) {
            TreatmentStageProgress progress = new TreatmentStageProgress();
            progress.setPlan(plan);
            progress.setServiceStage(stage);
            progress.setStatus(TreatmentStageStatus.NOT_STARTED);
            progress.setNotes("");
            progress.setDayStart(null);
            progress.setDayComplete(null);
            progresses.add(progress);
        }
        return progresses;
    }

    private TreatmentPlanResponse buildTreatmentPlanResponse(TreatmentPlan plan,
                                                             List<TreatmentStageProgress> progresses,
                                                             com.fuhcm.swp391.be.itmms.entity.service.Service service) {
        TreatmentPlanResponse response = modelMapper.map(plan, TreatmentPlanResponse.class);
        response.setServiceName(service.getServiceName());

        List<TreatmentStageProgressResponse> progressResponses = progresses.stream().map(progress -> {
            TreatmentStageProgressResponse progressResponse = modelMapper.map(progress, TreatmentStageProgressResponse.class);
            progressResponse.setStageName(progress.getServiceStage().getName());
            return progressResponse;
        }).toList();
        response.setTreatmentStageProgressResponses(progressResponses);
        response.setDateStart(plan.getDayStart());
        return response;
    }




}
