package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentPlanRequest;
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

    @GetMapping("/api/treatment-plan/medical-record/{medicalRecordId}")
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

    @PostMapping("/api/treatment-plan/medical-record")
    public ResponseEntity<?> createTreatmentPlan(@RequestBody TreatmentPlanRequest request) throws NotFoundException {
        TreatmentPlanResponse response = treatmentPlanService.createTreatmentPlanWithStages(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseFormat<>(HttpStatus.CREATED.value(), "CREATE_SUCCESS", "Tạo TreatmentPlan thành công", response)
        );
    }

}
