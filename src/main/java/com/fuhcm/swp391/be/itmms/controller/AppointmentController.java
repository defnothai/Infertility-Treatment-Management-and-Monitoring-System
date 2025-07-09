package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.AppointmentResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
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
}
