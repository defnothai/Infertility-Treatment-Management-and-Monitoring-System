package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.request.WeeklyScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.service.AuthenticationService;
import com.fuhcm.swp391.be.itmms.service.ScheduleService;
import com.fuhcm.swp391.be.itmms.service.ScheduleTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AuthenticationService authenticationService;

//    @PostMapping("/generate/staff")
//    public ResponseEntity<?> generateStaffSchedules(@RequestBody ScheduleRequest request,
//                                                    Authentication authentication) throws BadRequestException {
//        scheduleService.generateStaffSchedules(request.getStartDate(), request.getEndDate(), authentication);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
//                "SCHEDULES_CREATED_SUCCESS",
//                "Tạo các lịch làm việc thành công",
//                null));
//    }
//
//    @PostMapping("/generate/staff/template/{templateId}")
//    public ResponseEntity<?> generateStaffSchedulesForTemplate(
//            @PathVariable("templateId") Long templateId,
//            @RequestBody ScheduleRequest request,
//            Authentication authentication) throws NotFoundException {
//        scheduleService.generateSchedulesForTemplate(templateId, request.getStartDate(), request.getEndDate(), authentication);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
//                                                                    "SCHEDULED_CREATED_SUCCESS",
//                                                                    "Tạo các lịch làm việc thành công",
//                                                                    null));
//    }
//
//    @GetMapping("/staff")
//    public ResponseEntity getSchedulesByStaff(@RequestBody WeeklyScheduleRequest request,
//                                                              Authentication authentication) {
//        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
//                "FETCH_DATA_SUCCESS",
//                "Lấy dữ liệu thành công",
//                scheduleService.getSchedulesByWeekYear(request, authentication)));
//    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @Valid @RequestBody ScheduleRequest request,
            Authentication authentication) {
        scheduleService.createSchedule(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                                "SCHEDULE_CREATED_SUCCESS",
                                                                "Tạo lịch làm việc thành công",
                                                                null));
    }

    @GetMapping("/available-dates-by-doctor")
    public ResponseEntity<ApiResponse<?>> getAvailableDates(@RequestParam("id") @Min(1) Long id) {
        List<LocalDate> availableDates = scheduleService.getAvailableSchedulesByDoctor(id);
        if(availableDates.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không còn lịch trống", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableDates));
    }

    // ***
    @GetMapping("/my-available-dates")
    public ResponseEntity<ApiResponse<?>> getMyAvailableDates() {
        List<LocalDate> availableDates = scheduleService.getMyAvailableSchedules();
        if(availableDates.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không còn lịch trống", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableDates));
    }

    //***
    @GetMapping("/my-free-slot")
    public ResponseEntity<ApiResponse<?>> getMyAvailableSlots(
            @Valid @RequestParam("date") LocalDate date) {
        Long doctorId = authenticationService.getCurrentAccount().getId();
        List<LocalTime> availableSlots = scheduleService.getAvailableSlots(doctorId, date);
        if(availableSlots.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách slot không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách slot thành công", availableSlots));
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

    @GetMapping("/suggestion")
    public ResponseEntity<?> getSuggestionList(@Valid @RequestParam("id") @Min(1) Long id,
                                               @Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
                                               @Valid @RequestParam("toDate") @NotNull LocalDate toDate) {
        Map<LocalDate, SuggestionResponse> accounts = scheduleService.getSuggestionList(id, fromDate, toDate);
        if(accounts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Không có người gợi ý phù hợp",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách gợi ý thành công",
                        accounts));
    }

    @PutMapping
    public ResponseEntity<?> setReplaceEmployment(@Valid @RequestParam("replacedId") @Min(1) Long replacedId,
                                                  @Valid @RequestParam("suggestId") @Min(1) Long suggestId,
                                                  @Valid @RequestParam("workDate") @NotNull LocalDate workDate){
        boolean check = scheduleService.setReplaceEmployment(replacedId, suggestId, workDate);
        if(!check){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "UPDATE_DATA_FAIL",
                            "Cập nhật người thay ca thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "UPDATE_DATA_SUCCESS",
                        "Cập nhật người thay ca thành công",
                        null));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMineSchedule(Authentication authentication,
                                             @Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
                                             @Valid @RequestParam("toDate") @NotNull LocalDate toDate) {
        List<ScheduleResponse> responses = scheduleService.getMineSchedule(authentication, fromDate, toDate);
        if(responses.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Không có lịch làm việc",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách lịch làm việc thành công",
                        responses));
    }
}
