package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.MedicalRecordAccessRequest;
import com.fuhcm.swp391.be.itmms.dto.response.MedicalRecordAccessResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.MedicalRecordAccessService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-record-access")
@RequiredArgsConstructor
public class MedicalRecordAccessController {

    private final MedicalRecordAccessService medicalRecordAccessService;

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity getAccessListByMedicalRecordId(@PathVariable Long medicalRecordId) {
        List<MedicalRecordAccessResponse> response =
                medicalRecordAccessService.getAccessListByMedicalRecordId(medicalRecordId);
        ResponseFormat result = new ResponseFormat<>(
                200,
                "FETCH_SUCCESS",
                "Lấy danh sách quyền truy cập hồ sơ bệnh án thành công",
                response
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity createMedicalRecordAccess(
            @PathVariable Long medicalRecordId,
            @RequestBody MedicalRecordAccessRequest request
    ) throws NotFoundException {
        MedicalRecordAccessResponse response = medicalRecordAccessService.createAccess(medicalRecordId, request);
        ResponseFormat<MedicalRecordAccessResponse> result = new ResponseFormat<>(
                201,
                "CREATE_SUCCESS",
                "Cấp quyền truy cập hồ sơ thành công",
                response
        );
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/{accessId}")
    public ResponseEntity<ResponseFormat<MedicalRecordAccessResponse>> updateMedicalRecordAccess(
            @PathVariable Long accessId,
            @RequestBody MedicalRecordAccessRequest request
    ) throws NotFoundException {
        MedicalRecordAccessResponse response = medicalRecordAccessService.updateAccess(accessId, request);

        ResponseFormat<MedicalRecordAccessResponse> result = new ResponseFormat<>(
                200,
                "UPDATE_SUCCESS",
                "Cập nhật quyền truy cập hồ sơ thành công",
                response
        );

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{accessId}")
    public ResponseEntity<ResponseFormat<Void>> revokeMedicalRecordAccess(@PathVariable Long accessId) throws NotFoundException {
        medicalRecordAccessService.revokeAccess(accessId);
        return ResponseEntity.ok(
                new ResponseFormat<>(200, "DELETE_SUCCESS", "Thu hồi quyền truy cập thành công", null)
        );
    }


}

