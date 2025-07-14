package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.UpdateDiagnosisSymptom;
import com.fuhcm.swp391.be.itmms.dto.response.EmploymentMedicalRecordResponse;
import com.fuhcm.swp391.be.itmms.dto.response.MedicalRecordResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.MedicalRecordService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // api get medical record dành cho bác sĩ
    @GetMapping("/api/medical-record/{accountId}")
    public ResponseEntity getMedicalRecord(@PathVariable("accountId") Long accountId) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "FETCH_SUCCESS",
                                                        "Lấy hồ sơ bênh nhân thành công",
                                                        medicalRecordService.getMedicalRecord(accountId)));
    }

    @PutMapping("/api/medical-record/{medicalRecordId}")
    public ResponseEntity<?> updateMedicalRecord(
            @PathVariable("medicalRecordId") Long medicalRecordId,
            @RequestBody UpdateDiagnosisSymptom request
    ) throws NotFoundException {
        UpdateDiagnosisSymptom response = medicalRecordService.updateDiagnosisAndSymptoms(medicalRecordId, request);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật hồ sơ bệnh nhân thành công",
                        response
                )
        );
    }

    // api get medical record dành cho bệnh nhân
    @GetMapping("/api/medical-record/me")
    public ResponseEntity getMedicalRecord() throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy hồ sơ thành công",
                medicalRecordService.getUserMedicalRecord()));
    }

    // danh sách medical record dành cho manager
    @GetMapping("/api/manage/medical-record")
    public ResponseEntity getMedicalRecordsForEmployment() {
        List<EmploymentMedicalRecordResponse> list = medicalRecordService.getAllMedicalRecordsForEmployment();
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy dữ liệu thành công", list));
    }




}
