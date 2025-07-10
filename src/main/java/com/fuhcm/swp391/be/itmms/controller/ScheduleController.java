package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.request.WeeklyScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.service.ScheduleService;
import com.fuhcm.swp391.be.itmms.service.ScheduleTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/generate/staff")
    public ResponseEntity<?> generateStaffSchedules(@RequestBody ScheduleRequest request,
                                                    Authentication authentication) throws BadRequestException {
        scheduleService.generateStaffSchedules(request.getStartDate(), request.getEndDate(), authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                "SCHEDULES_CREATED_SUCCESS",
                "Tạo các lịch làm việc thành công",
                null));
    }

    @PostMapping("/generate/staff/template/{templateId}")
    public ResponseEntity<?> generateStaffSchedulesForTemplate(
            @PathVariable("templateId") Long templateId,
            @RequestBody ScheduleRequest request,
            Authentication authentication) throws NotFoundException {
        scheduleService.generateSchedulesForTemplate(templateId, request.getStartDate(), request.getEndDate(), authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                                    "SCHEDULED_CREATED_SUCCESS",
                                                                    "Tạo các lịch làm việc thành công",
                                                                    null));
    }

    @GetMapping("/staff")
    public ResponseEntity getSchedulesByStaff(@RequestBody WeeklyScheduleRequest request,
                                                              Authentication authentication) {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy dữ liệu thành công",
                scheduleService.getSchedulesByWeekYear(request, authentication)));
    }

    @GetMapping("/available-dates-by-doctor")
    public ResponseEntity<ApiResponse<?>> getAvailableDates(@RequestParam("id") @Min(1) Long id) {
        List<LocalDate> availableDates = scheduleService.getAvailableSchedulesByDoctor(id);
        if(availableDates.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không còn lịch trống", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableDates));
    }

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<?>> getAvailableSlots(
            @Valid @RequestParam("id") Long id,
            @Valid @RequestParam("date") LocalDate date) {
        List<LocalTime> availableSlots = scheduleService.getAvailableSlots(id, date);
        if(availableSlots.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách slot không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách slot thành công", availableSlots));
    }

    @GetMapping("/available-dates-by-date")
    public ResponseEntity<ApiResponse<?>> getAvailableDatesByDate() {
        List<LocalDate> availableDates = scheduleService.getAvailableDatesByDate();
        if(availableDates.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công",  availableDates));
    }

    @GetMapping("/available-slots-by-date")
    public ResponseEntity<ApiResponse<?>> getAvailableSlotsByDate(@Valid @RequestParam("date") LocalDate date) {
        List<LocalTime> availableSlots = scheduleService.getAvailableSlotsByDate(date);
        if(availableSlots.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableSlots));
    }
}
