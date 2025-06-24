package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponse;
import com.fuhcm.swp391.be.itmms.service.ServiceService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @GetMapping("/api/list/services")
    public ResponseEntity getServicesInListPage() {
        return serviceService.getAllServicesInListPage();
    }

    @GetMapping("/api/manager/services")
    public ResponseEntity getAllServices() {
        return serviceService.getAllServices();
    }

    @PostMapping("/api/manager/services")
    public ResponseEntity createService(@ModelAttribute ServiceRequest serviceRequest) throws IOException {
        return serviceService.createService(serviceRequest);
    }

    @PutMapping("/api/manager/services/{id}")
    public ResponseEntity updateService(@PathVariable Long id,
                                        @ModelAttribute ServiceRequest serviceRequest) throws IOException, NotFoundException {
        return serviceService.updateService(id, serviceRequest);
    }


}
