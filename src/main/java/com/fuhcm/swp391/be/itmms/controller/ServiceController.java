package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponse;
import com.fuhcm.swp391.be.itmms.service.ServiceService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        serviceService.getAllServicesInHomePage())
        );
    }

    @GetMapping("/api/list/services")
    public ResponseEntity getServicesInListPage() {
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        serviceService.getAllServicesInListPage())
        );
    }

    @GetMapping("/api/manage/services")
    public ResponseEntity getAllServices() {
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        serviceService.getAllServices())
        );
    }

    @PostMapping("/api/manage/services")
    public ResponseEntity createService(@RequestBody ServiceRequest serviceRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                        "SERVICE_CREATED_SUCCESS",
                        "Tạo mới dịch vụ thành công",
                        serviceService.createService(serviceRequest)));
    }

    @PutMapping("/api/manage/services/{id}")
    public ResponseEntity updateService(@PathVariable Long id,
                                        @RequestBody ServiceRequest serviceRequest) throws IOException, NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "SERVICE_UPDATED_SUCCESS",
                "Cập nhật dịch vụ thành công",
                serviceService.updateService(id, serviceRequest)));
    }



}
