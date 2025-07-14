package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.PrescriptionRequest;
import com.fuhcm.swp391.be.itmms.dto.request.PrescriptionUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.PrescriptionResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.PrescriptionService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/api/prescription")
    public ResponseEntity createPrescription(@RequestBody PrescriptionRequest request) throws NotFoundException {
        PrescriptionResponse response = prescriptionService.createPrescription(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(
                        HttpStatus.CREATED.value(),
                        "CREATE_SUCCESS",
                        "Tạo đơn thuốc thành công",
                        response));
    }

    @PutMapping("/api/prescription/{id}")
    public ResponseEntity updatePrescription(
            @PathVariable Long id,
            @RequestBody PrescriptionUpdateRequest request
    ) throws NotFoundException {
        PrescriptionResponse response = prescriptionService.updatePrescription(id, request);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                "UPDATE_SUCCESS", "Cập nhật đơn thuốc thành công", response));
    }

    @GetMapping("/api/prescription/{sessionId}")
    public ResponseEntity getAllBySession(@PathVariable Long sessionId) throws NotFoundException {
        List<PrescriptionResponse> responses = prescriptionService.getAllBySessionId(sessionId);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy dữ liệu đơn thuốc thành công",
                responses
        ));
    }



}
