package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanRequest;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentPlanResponse;
import com.fuhcm.swp391.be.itmms.service.TreatmentPlanService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    // get plan + stage
    @GetMapping("/api/medical-record/{medicalRecordId}/treatment-plan")
    public ResponseEntity<?> getTreatmentPlansByMedicalRecordId(@PathVariable("medicalRecordId") Long medicalRecordId) throws NotFoundException {
        List<TreatmentPlanResponse> responses = treatmentPlanService.getByMedicalRecordId(medicalRecordId);
        if (responses.isEmpty()) {
            throw new NotFoundException("Chưa có phác đồ điều trị bệnh");
        }
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        responses
                )
        );
    }

    // create plan + stage
    @PostMapping("/api/medical-record/treatment-plan")
    public ResponseEntity<?> createTreatmentPlan(@RequestBody TreatmentPlanRequest request) throws NotFoundException {
        TreatmentPlanResponse response = treatmentPlanService.createTreatmentPlanWithStages(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseFormat<>(HttpStatus.CREATED.value(), "CREATE_SUCCESS", "Tạo TreatmentPlan thành công", response)
        );
    }

    // *** NEW:
    // bác sĩ:
    // cân nhắc hủy khi không theo phác đồ
    // cân nhắc hủy khi lỡ hẹn quá 3 lần
    // có thể update lại nếu lỡ hủy
    @PutMapping("/api/medical-record/treatment-plan/{planId}")
    public ResponseEntity updateTreatmentPlan(@PathVariable("planId") Long planId, @RequestBody TreatmentPlanUpdateRequest request) throws NotFoundException {
        TreatmentPlanResponse updatedPlan = treatmentPlanService.updateTreatmentPlan(planId, request);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "UPDATE_SUCCESS",
                "Cập nhật phác đồ điều trị thành công",
                updatedPlan));
    }


}
