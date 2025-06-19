package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")

public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/api/home/services")
    public ResponseEntity getServicesInHomePage() {
        return serviceService.getAllServicesInHomePage();
    }

}
