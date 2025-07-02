package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.PatientInfoDetails;
import com.fuhcm.swp391.be.itmms.dto.request.MedicalRecordRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.service.MedicalRecordService;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/api/medical-record/{userId}")
    public ResponseEntity getMedicalRecord(@PathVariable("userId") Long userId) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "FETCH_SUCCESS",
                                                        "Lấy hồ sơ bênh nhân thành công",
                                                        medicalRecordService.getMedicalRecord(userId)));
    }

    @PutMapping("/api/medical-record/{recordId}")
    public ResponseEntity updateMedicalRecord(@PathVariable("recordId") Long recordId,
                                              @RequestBody MedicalRecordRequest medicalRecordRequest) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "MEDICAL_RECORD_UPDATED_SUCCESS",
                                                        "Cập nhật hồ sơ bệnh án thành công",
                                                        medicalRecordService.updateMedicalRecord(recordId, medicalRecordRequest)));
    }


}
