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
import org.springframework.cglib.core.Local;
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
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest, Authentication authentication) {
        Appointment appointment = appointmentService.createNewAppointment(appointmentRequest, authentication);
        if(appointment == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "CREATE_APPOINTMENT_FAILED",
                            "Tạo appointment thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                        "CREATE_APPOINTMENT_SUCCESS",
                        "Tạo appointment thành công",
                        null));
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
        if(response.isEmpty()){
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

    @GetMapping("/report/list-appointments")
    public ResponseEntity<?> getListAppointmentsForReport(
            @Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
            @Valid @RequestParam("toDate") @NotNull LocalDate toDate
    ){
        List<AppointmentResponse> responses = appointmentService.getListAppointmentsForReport(fromDate, toDate);
        if(responses.isEmpty()){
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
                        responses));
    }

    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/update-status")
    public ResponseEntity<?> updateAppointmentStatus(@Valid @RequestParam("id") @NotNull Long id){
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Cập nhật appointment thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Cập nhật appointment thành công",
                        response));
    }
}
