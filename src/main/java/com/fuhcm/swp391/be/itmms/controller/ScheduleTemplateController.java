package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleTemplateResponse;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.service.ScheduleTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/schedule-template")
public class ScheduleTemplateController {

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;

    @PostMapping
    public ResponseEntity<?> createScheduleTemplate(@Valid @RequestBody ScheduleTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                                "TEMPLATE_CREATED_SUCCESS",
                                                                "Tạo thành công mẫu lịch làm việc",
                                                                    scheduleTemplateService.createTemplateStaff(request)));
    }

    @PutMapping
    public ResponseEntity<?> updateScheduleTemplate(@Valid @RequestBody ScheduleTemplateRequest request,
                                                    @Valid @RequestParam("id") @Min(1) Long id) {
        ScheduleTemplateResponse response = scheduleTemplateService.updateScheduleTemplate(request,id);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "TEMPLATE_UPDATED_FAILURE",
                            "Cập nật mẫu lịch làm việc thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "TEMPLATE_UPDATED_SUCCESS",
                        "Cập nhật mẫu lịch làm việc thành công",
                        response));
    }

    @GetMapping
    public ResponseEntity<?> getAllScheduleTemplate() {
        List<ScheduleTemplateResponse> responses = scheduleTemplateService.getAllScheduleTemplate();
        if(responses == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "TEMPLATE_FETCH_FAIL",
                            "Lấy danh sách mẫu lịch làm việc thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "TEMPLATE_FETCH_SUCCESS",
                        "Lấy danh sách mẫu lịch làm việc thành công",
                        responses));
    }

}
