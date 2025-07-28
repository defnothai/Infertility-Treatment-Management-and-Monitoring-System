package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.UpdateDiagnosisSymptom;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.service.MedicalRecordService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    // ================================================ DOCTOR =========================================================

    // lấy danh sách bệnh nhân đã tiếp nhận cho bác sĩ
    @GetMapping("/api/patient/me")
    public ResponseEntity getMyPatients(@RequestParam(required = false) String keyword) throws NotFoundException {
        List<AccountResponse> response;
        if (keyword == null || keyword.trim().isEmpty()) {
            response = medicalRecordService.getMyPatient();
        }else {
            response = medicalRecordService.searchByKeyWord(keyword);
        }
        if (response.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bệnh nhân");
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy thông tin thành công",
                response));
    }


    // lấy danh sách lịch sử khám bệnh cho doctor
    @GetMapping("/api/medical-record/history/{accountId}")
    public ResponseEntity getMedicalRecordsByAccountId(@PathVariable Long accountId) {
        List<MedicalRecordSummaryResponse> records = medicalRecordService.getMedicalRecordsByAccountId(accountId);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy lịch sử khám bệnh thành công",
                records));
    }
    // create một hồ sơ trong danh sách khám bệnh
    @PostMapping("/api/medical-record/new/{accountId}")
    public ResponseEntity createNewMedicalRecord(@PathVariable Long accountId) {
        MedicalRecordSummaryResponse record = medicalRecordService.createNewMedicalRecord(accountId);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                "CREATE_SUCCESS",
                "Tạo hồ sơ thành công",
                record));
    }

    // api get medical record cụ thể dành cho bác sĩ
    @GetMapping("/api/medical-record/{recordId}")
    public ResponseEntity getMedicalRecord(@PathVariable("recordId") Long recordId) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "FETCH_SUCCESS",
                                                        "Lấy hồ sơ bênh nhân thành công",
                                                        medicalRecordService.getMedicalRecord(recordId)));
    }

    // điền triệu chứng, kết luận ban đầu
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

    // ============================================== BỆNH NHÂN ===================================================================

    // lấy danh sách lịch sử khám bệnh cho bệnh nhân
    @GetMapping("/api/medical-record/history/me")
    public ResponseEntity getMedicalRecordsByAccountId() {
        List<MedicalRecordSummaryResponse> records = medicalRecordService.getMyMedicalRecords();
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy lịch sử khám bệnh thành công",
                records));
    }

    // api get medical record cụ thể dành cho bệnh nhân
    @GetMapping("/api/medical-record/me/{recordId}")
    public ResponseEntity getMyMedicalRecord(@PathVariable Long recordId) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy hồ sơ thành công",
                medicalRecordService.getUserMedicalRecord(recordId)));
    }

    // danh sách medical record dành cho manager
    @GetMapping("/api/manage/medical-record")
    public ResponseEntity getMedicalRecordsForEmployment() {
        List<EmploymentMedicalRecordResponse> list = medicalRecordService.getAllMedicalRecordsForEmployment();
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy dữ liệu thành công", list));
    }

    // chi tiết medical record cho manager
//    @GetMapping("/api/manage/medical-record/{accountId}")
//    public ResponseEntity getManagerMedicalRecord(@PathVariable Long accountId) throws NotFoundException {
//        ManagerMedicalRecordResponse response = medicalRecordService.getManagerMedicalRecord(accountId);
//        ResponseFormat<ManagerMedicalRecordResponse> result = new ResponseFormat<>(
//                200,
//                "FETCH_SUCCESS",
//                "Lấy thông tin hồ sơ bệnh án thành công",
//                response
//        );
//        return ResponseEntity.ok(result);
//    }

}
