package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceDetailsRequest;
import com.fuhcm.swp391.be.itmms.service.ServiceDetailsService;

import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ServiceDetailsController {

    private final ServiceDetailsService serviceDetailsService;

    public ServiceDetailsController(ServiceDetailsService serviceDetailsService) {
        this.serviceDetailsService = serviceDetailsService;
    }

    @GetMapping("api/service-detail/{serviceId}")
    public ResponseEntity getServiceDetails(@PathVariable Long serviceId) throws NotFoundException {
        return serviceDetailsService.getServiceDetails(serviceId);
    }

    @PostMapping("api/service-detail/{serviceId}")
    public ResponseEntity createServiceDetails(@PathVariable Long serviceId,
                                               @ModelAttribute ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException, IOException {
        return serviceDetailsService.createServiceDetails(serviceId, serviceDetailsRequest);
    }


}
