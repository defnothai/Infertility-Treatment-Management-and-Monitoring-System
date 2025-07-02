package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private AccountRepository accountRepository;
    
    @GetMapping("/view")
    public ResponseEntity<ApiResponse<?>> getSchedule(Authentication authentication) {
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Schedule> doctorSchedules = scheduleService.getSchedules(account.getId());
        if(doctorSchedules.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lịch làm vệc không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lịch làm việc thành công", doctorSchedules));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createSchedule(@RequestBody @Valid ScheduleRequest request, Authentication authentication) {
        if(!request.getFrom().isBefore(request.getTo())){
            return ResponseEntity.ok(new ApiResponse<>(true, "Ngày bắt đầu phải trước ngày kết thúc", null));
        }
        scheduleService.generateSchedules(request.getFrom(), request.getTo(), authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo schedule thành công từ " + request.getFrom() + " đến " + request.getTo(), null));
    }

    @GetMapping("/available-dates-by-doctor")
    public ResponseEntity<ApiResponse<?>> getAvailableDates(@RequestParam Long id) {
        List<LocalDate> availableDates = scheduleService.getAvailableSchedulesByDoctor(id);
        if(availableDates.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không còn lịch trống", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableDates));
    }

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<?>> getAvailableSlots(@Valid @RequestParam Long id,@Valid @RequestParam LocalDate date) {
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
    public ResponseEntity<ApiResponse<?>> getAvailableSlotsByDate(@Valid @RequestParam LocalDate date) {
        List<LocalTime> availableSlots = scheduleService.getAvailableSlotsByDate(date);
        if(availableSlots.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", availableSlots));
    }
}
