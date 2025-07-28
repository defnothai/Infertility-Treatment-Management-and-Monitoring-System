package com.fuhcm.swp391.be.itmms.controller;


import com.fuhcm.swp391.be.itmms.dto.request.AppointmentRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentReportResponse;
import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/test")
    public ResponseEntity getAllAppointments() {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy danh sách lịch hẹn thành công",
                        appointmentService.getAllAppointments()
                )
        );
    }

    @GetMapping("/report")
    public ResponseEntity<?> getAppointmentReport(@Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
                                                  @Valid @RequestParam("toDate") @NotNull LocalDate toDate) throws NotFoundException {
        List<AppointmentReportResponse> response = appointmentService.getAppointmentReport(fromDate, toDate);
        if(response == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy danh sách tài khoản thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách tài khoản thành công",
                        response));
    }

    // danh sách buổi khám cho staff
    @GetMapping("/staff/filter-appointment")
    public ResponseEntity searchAppointments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId
    ) throws NotFoundException {
        if (keyword == null || keyword.trim().isEmpty() ) {
            keyword = "";
        }
        List<AppointmentResponse> response = appointmentService.searchAppointments(keyword, date, doctorId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách cuộc hẹn thành công thành công",
                        response));
    }



}
