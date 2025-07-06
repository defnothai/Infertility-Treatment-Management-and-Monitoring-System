package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceStageRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentPlanResponse;
import com.fuhcm.swp391.be.itmms.service.ServiceStageService;
import com.fuhcm.swp391.be.itmms.service.TreatmentPlanService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceStageController {

    private final ServiceStageService serviceStageService;

    public ServiceStageController(ServiceStageService serviceStageService) {
        this.serviceStageService = serviceStageService;
    }

    @PostMapping("/api/manage/services/{serviceId}/service-stage")
    public ResponseEntity createServiceStages(@PathVariable Long serviceId,
                                              @RequestBody List<ServiceStageRequest> stages) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                        "SERVICE_STAGES_CREATED_SUCCESS",
                                                        "Tạo mới các giai đoạn của dịch vụ thành công",
                                                            serviceStageService.createServiceStages(serviceId, stages)));
    }

    @PutMapping("/api/manage/services/{serviceId}/service-stage/{stageId}")
    public ResponseEntity updateServiceStage(@PathVariable("stageId") Long stageId,
                                             @RequestBody ServiceStageRequest request) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                        "SERVICE_STAGES_UPDATED_SUCCESS",
                        "Cập nhật giai đoạn thành công",
                        serviceStageService.updateServiceStage(stageId, request)));
    }

    @DeleteMapping("/api/manage/services/{serviceId}/service-stage/{stageId}")
    public ResponseEntity deleteServiceStage(@PathVariable("stageId") Long stageId) throws NotFoundException {
        serviceStageService.deleteServiceStage(stageId);
        return ResponseEntity.noContent().build();
    }




}
