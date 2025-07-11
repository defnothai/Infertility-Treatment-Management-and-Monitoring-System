package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.service.AppointmentService;
import com.fuhcm.swp391.be.itmms.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getAppointmentsByDoctorId(@Valid Authentication authentication) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(authentication);
        if(appointments.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không có appointment nào", null));
        }
        return  ResponseEntity.ok(new ApiResponse<>(true, "Appointment found", appointments));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest, Authentication authentication) {
        Appointment appointment = appointmentService.createNewAppointment(appointmentRequest, authentication);
        if(appointment == null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo appointment không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo appointment thành công", null));
    }

    @PutMapping("/confirm-appointment")
    public ResponseEntity<ApiResponse<?>> updateAppointment(HttpServletRequest request ,
                                                            @RequestParam("date") LocalDate date) {
        boolean check = appointmentService.updateAppointment(request, date);
        if(check){
            return ResponseEntity.ok(new ApiResponse<>(true, "Update thanh cong", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Update khong thanh cong", null));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/last-appointment")
    public ResponseEntity<ApiResponse<?>> getLastAppointment(Authentication authentication) {
        AppointmentResponse appointmentResponse = appointmentService.getLastAppointment(authentication);
        if(appointmentResponse == null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Khoong có appointment gần nhất", appointmentResponse));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment found", appointmentResponse));
    }

}
