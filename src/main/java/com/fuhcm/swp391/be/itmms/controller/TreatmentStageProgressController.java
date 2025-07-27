package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.TreatmentStageProgressRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.service.TreatmentStageProgressService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TreatmentStageProgressController {

    private final TreatmentStageProgressService progressService;

    public TreatmentStageProgressController(TreatmentStageProgressService progressService) {
        this.progressService = progressService;
    }

    // hoàn thành giai đoạn
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

    // get status của stage sau khi tạo ra session
    // vì gấp nên bỏ
    @GetMapping("/api/medical-record/treatment-stage-progress/{id}")
    public ResponseEntity getProgressStatus(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity
                .ok(new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy trạng thái giai đoạn thành công",
                        progressService.getProgressStatus(id)));
    }

}
