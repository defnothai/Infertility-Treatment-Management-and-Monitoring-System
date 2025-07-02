package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.service.ScheduleTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('MANAGER')")
@RequestMapping("/api/schedulesTemplate")
public class ScheduleTemplateController {

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createScheduleTemplate(@Valid @RequestBody ScheduleTemplateRequest request) {
        ScheduleTemplate scheduleTemplate = scheduleTemplateService.createTemplate(request);
        if(scheduleTemplate == null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo template thất bại", null));
        }
        return  ResponseEntity.ok(new ApiResponse<>(true, "Tạo template thành công", null));
    }
}
