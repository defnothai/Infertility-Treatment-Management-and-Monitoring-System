package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.ConsultationRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ConsultationResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.ConsultationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping
    public ResponseEntity getAllConsultations() {
        List<ConsultationResponse> response = consultationService.getAllConsultations();
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy danh sách consultation thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy danh sách consultation thành công",
                response));
    }

    @PostMapping
    public ResponseEntity createConsultation(
            @Valid @RequestBody ConsultationRequest consultationRequest
    ) {
        ConsultationResponse consultationResponse = consultationService.createConsultation(consultationRequest);
        if(consultationResponse == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Tạo consultation thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Tạo consultation thành công",
                consultationResponse));
    }


    @PutMapping
    public ResponseEntity updateConsultation(
            Authentication authentication,
            @RequestParam("id") @Min(1) Long id,
            @RequestParam("status") @NotBlank String status
    ) {
        ConsultationResponse response = consultationService.updateConsultation(authentication, id, status);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Cập nhật consultation thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Cập nhật consultation thành công",
                null));
    }

    @DeleteMapping
    public ResponseEntity deleteConsultation(
            Authentication authentication,
            @RequestParam("id") @Min(1) Long id
    ) {
        ConsultationResponse response = consultationService.deleteConsultation(authentication, id);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Xóa consultation thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Xóa consultation thành công",
                null));
    }
}
