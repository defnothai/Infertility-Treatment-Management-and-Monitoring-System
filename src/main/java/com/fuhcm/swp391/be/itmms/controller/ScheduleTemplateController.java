package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.service.ScheduleTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleTemplateController {

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;

    @PostMapping("/api/schedule-template/staff-template")
    public ResponseEntity<?> createScheduleTemplate(@RequestBody ScheduleTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                                "TEMPLATE_CREATED_SUCCESS",
                                                                "Tạo thành công mẫu lịch làm việc",
                                                                    scheduleTemplateService.createTemplateStaff(request)));
    }

}
