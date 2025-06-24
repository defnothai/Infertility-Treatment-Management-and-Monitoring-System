package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.service.ServiceDetailsService;

import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceDetailsController {

    private final ServiceDetailsService serviceDetailsService;

    public ServiceDetailsController(ServiceDetailsService serviceDetailsService) {
        this.serviceDetailsService = serviceDetailsService;
    }

    @GetMapping("api/chi-tiet-phuong-phap/{serviceId}")
    public ResponseEntity getServiceDetails(@PathVariable Long serviceId) throws NotFoundException {
        return serviceDetailsService.getServiceDetails(serviceId);
    }



}
