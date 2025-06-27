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

    @GetMapping("api/service-details/{serviceId}")
    public ResponseEntity getServiceDetails(@PathVariable Long serviceId) throws NotFoundException {
        return serviceDetailsService.getServiceDetails(serviceId);
    }

    @PostMapping("api/service-details/{serviceId}")
    public ResponseEntity createServiceDetails(@PathVariable Long serviceId,
                                               @RequestBody ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException, IOException {
        return serviceDetailsService.createServiceDetails(serviceId, serviceDetailsRequest);
    }

    @PutMapping("api/service-details/{id}")
    public ResponseEntity updateServiceDetails(@PathVariable Long id,
                                               @RequestBody ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException, IOException {
        return serviceDetailsService.updateServiceDetails(id, serviceDetailsRequest);
    }


}
