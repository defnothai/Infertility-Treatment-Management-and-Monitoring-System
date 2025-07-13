package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.FollowUpRequest;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentSessionRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.SessionDetailsResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentSessionResponse;
import com.fuhcm.swp391.be.itmms.service.TreatmentSessionService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TreatmentSessionController {

    private final TreatmentSessionService treatmentSessionService;

    // =================================================================================

    @GetMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions")
    public ResponseEntity getByProgress(@PathVariable Long progressId) throws NotFoundException {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu các buổi khám thành công",
                        treatmentSessionService.getByProgressId(progressId)));
    }

    @DeleteMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions/{sessionId}")
    public ResponseEntity<?> softDelete(@PathVariable Long sessionId) throws NotFoundException {
        treatmentSessionService.softDeleteById(sessionId);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "DELETE_SUCCESS",
                        "Xóa buổi khám thành công",
                        null
                )
        );
    }

    @PutMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions/{sessionId}")
    public ResponseEntity<?> updateSession(
            @PathVariable Long sessionId,
            @RequestBody TreatmentSessionRequest request
    ) throws NotFoundException {
        TreatmentSessionResponse response = treatmentSessionService.update(sessionId, request);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật buổi khám thành công",
                        response
                )
        );
    }

    @GetMapping("/api/treatment-sessions/{sessionId}/details")
    public ResponseEntity<?> getSessionDetail(@PathVariable Long sessionId) throws NotFoundException {
        SessionDetailsResponse response = treatmentSessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy chi tiết buổi điều trị thành công",
                response
        ));
    }


    // ====================================================================================================

    // bên fe chưa thay đổi phần này
    @PostMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions")
    public ResponseEntity<?> createFollowUpSession(
            @PathVariable Long progressId,
            @RequestBody FollowUpRequest request
    ) throws NotFoundException {
        TreatmentSessionResponse response = treatmentSessionService.createFollowUpSession(progressId, request);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "CREATE_SUCCESS",
                        "Tạo lịch tái khám thành công",
                        response
                )
        );
    }

    @PutMapping("/api/treatment-sessions/{sessionId}")
    public ResponseEntity<?> updateFollowUpSession(
            @PathVariable Long sessionId,
            @RequestBody FollowUpRequest request
    ) throws NotFoundException {
        TreatmentSessionResponse response = treatmentSessionService.updateFollowUpSession(sessionId, request);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "UPDATE_SUCCESS",
                "Cập nhật lịch tái khám thành công",
                response
        ));
    }

    @DeleteMapping("/api/treatment-sessions/{sessionId}")
    public ResponseEntity<?> deleteFollowUpSession(@PathVariable Long sessionId) throws NotFoundException {
        treatmentSessionService.deleteFollowUpSession(sessionId);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "DELETE_SUCCESS",
                "Xóa lịch tái khám thành công",
                null
        ));
    }





}
