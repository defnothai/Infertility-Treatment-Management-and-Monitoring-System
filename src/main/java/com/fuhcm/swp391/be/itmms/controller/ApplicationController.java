package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ApplicationRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApplicationResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.ApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF')")
    @PostMapping
    public ResponseEntity sendApplication(
            @Valid @RequestBody ApplicationRequest applicationRequest,
            Authentication authentication) {
        ApplicationResponse applicationResponse = applicationService.createApplication(applicationRequest
                ,authentication);
        if(applicationResponse == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseFormat<>(HttpStatus.BAD_REQUEST.value(),
                            "FETCH_DATA_FAIL",
                            "Tạo application thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Tạo application thành công",
                applicationResponse));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity getAllApplications() {
        List<ApplicationResponse> applicationResponses = applicationService.getAllApplications();
        if(applicationResponses == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseFormat<>(HttpStatus.BAD_REQUEST.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy application thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy application thành công",
                applicationResponses));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping
    public ResponseEntity updateApplication(
            @Valid @RequestParam("status") String status,
            @Valid @RequestParam("id") @Min(1) Long id){
        ApplicationResponse applicationResponse = applicationService.updateApplication(status, id);
        if(applicationResponse == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseFormat<>(HttpStatus.BAD_REQUEST.value(),
                            "FETCH_DATA_FAIL",
                            "Update application thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Update application thành công",
                applicationResponse));
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'STAFF')")
    @GetMapping("/mine")
    public ResponseEntity getMineApplications(Authentication authentication) {
        List<ApplicationResponse> applicationResponses = applicationService.getMineApplications(authentication);
        if(applicationResponses == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseFormat<>(HttpStatus.BAD_REQUEST.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy application thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy application thành công",
                applicationResponses));
    }
}
