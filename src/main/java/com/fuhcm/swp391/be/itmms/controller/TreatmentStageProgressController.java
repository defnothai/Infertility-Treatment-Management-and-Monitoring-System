package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentStageProgressRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.service.TreatmentStageProgressService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TreatmentStageProgressController {

    private final TreatmentStageProgressService progressService;

    public TreatmentStageProgressController(TreatmentStageProgressService progressService) {
        this.progressService = progressService;
    }

    @PutMapping("/api/medical-record/treatment-stage-progress/{id}")
    public ResponseEntity update(
            @PathVariable Long id,
            @RequestBody TreatmentStageProgressRequest request
    ) throws NotFoundException {
        return ResponseEntity
                .ok(new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                      "Cập nhật giai đoạn thành công",
                progressService.update(id, request)));
    }

}
