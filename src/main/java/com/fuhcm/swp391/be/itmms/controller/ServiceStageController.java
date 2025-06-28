package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceStageRequest;
import com.fuhcm.swp391.be.itmms.service.ServiceStageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceStageController {

    private final ServiceStageService serviceStageService;
    public ServiceStageController(ServiceStageService serviceStageService) {
        this.serviceStageService = serviceStageService;
    }

    @PostMapping("/api/manage/service-stage/{serviceId}")
    public ResponseEntity createServiceStage(@PathVariable Long serviceId,
                                             @RequestBody ServiceStageRequest serviceStageRequest) {
        return null;
    }

}
