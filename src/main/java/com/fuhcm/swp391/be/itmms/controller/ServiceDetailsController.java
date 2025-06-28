package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceDetailsRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.ServiceDetailsService;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        serviceDetailsService.getServiceDetails(serviceId))
        );
    }

    @PostMapping("api/manage/service-details/{serviceId}")
    public ResponseEntity createServiceDetails(@PathVariable Long serviceId,
                                               @RequestBody ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException, IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                        "SERVICE_DETAILS_CREATED_SUCCESS",
                        "Tạo mới chi tiết dịch vụ thành công",
                        serviceDetailsService.createServiceDetails(serviceId, serviceDetailsRequest)));
    }

    @PutMapping("api/manage/service-details/{id}")
    public ResponseEntity updateServiceDetails(@PathVariable Long id,
                                               @RequestBody ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException, IOException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.CREATED.value(),
                "SERVICE_DETAILS_UPDATED_SUCCESS",
                "Cập nhật chi tiết dịch vụ thành công",
                serviceDetailsService.updateServiceDetails(id, serviceDetailsRequest)));
    }


}
