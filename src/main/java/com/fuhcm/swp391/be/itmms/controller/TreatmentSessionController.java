package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.FollowUpDTO;
import com.fuhcm.swp391.be.itmms.dto.request.FollowUpDeleteRequest;
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

    // lấy danh sách các treatment session bằng treatment stage progress
    // cái này fe đã có
    @GetMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions")
    public ResponseEntity getByProgress(@PathVariable Long progressId) throws NotFoundException {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu các buổi khám thành công",
                        treatmentSessionService.getByProgressId(progressId)));
    }

    // tạo session bằng hẹn tái khám
    @PostMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions")
    public ResponseEntity<?> createFollowUpSession(
            @PathVariable Long progressId,
            @RequestBody FollowUpDTO request
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

    // lấy chi tiết lịch đã hẹn
    @GetMapping("/api/treatment-sessions/{sessionId}/follow-up-details")
    public ResponseEntity<?> getFollowUpDetail(@PathVariable Long sessionId) throws NotFoundException {
        FollowUpDTO response = treatmentSessionService.getFollowUpDetail(sessionId);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy chi tiết lịch tái khám thành công",
                response
        ));
    }

    // update lịch đã hẹn
    @PutMapping("/api/treatment-sessions/{sessionId}")
    public ResponseEntity<?> updateFollowUpSession(
            @PathVariable Long sessionId,
            @RequestBody FollowUpDTO request
    ) throws NotFoundException {
        TreatmentSessionResponse response = treatmentSessionService.updateFollowUpSession(sessionId, request);
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "UPDATE_SUCCESS",
                "Cập nhật lịch tái khám thành công",
                response
        ));
    }


    // cập nhật triệu chứng, kết luận khi khám
    // cũng như là hoàn tất buổi khám
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

    // lấy chi tiết session (new)
    @GetMapping("/api/treatment-sessions/{sessionId}")
    public ResponseEntity<?> getSessionDetail(@PathVariable Long sessionId) throws NotFoundException {
        TreatmentSessionResponse response = treatmentSessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật buổi khám thành công",
                        response
                )
        );
    }



//        @GetMapping("/api/treatment-sessions/{sessionId}/details")
//    public ResponseEntity<?> getSessionDetail(@PathVariable Long sessionId) throws NotFoundException {
//        SessionDetailsResponse response = treatmentSessionService.getSessionDetail(sessionId);
//        return ResponseEntity.ok(new ResponseFormat<>(
//                HttpStatus.OK.value(),
//                "FETCH_SUCCESS",
//                "Lấy chi tiết buổi điều trị thành công",
//                response
//        ));
//    }

    //    @DeleteMapping("/api/treatment-stage-progress/{progressId}/treatment-sessions/{sessionId}")
//    public ResponseEntity<?> softDelete(@PathVariable Long sessionId) throws NotFoundException {
//        treatmentSessionService.softDeleteById(sessionId);
//        return ResponseEntity.ok(
//                new ResponseFormat<>(
//                        HttpStatus.OK.value(),
//                        "DELETE_SUCCESS",
//                        "Xóa buổi khám thành công",
//                        null
//                )
//        );
//    }

    // hủy lịch hẹn
//    @DeleteMapping("/api/treatment-sessions/{sessionId}")
//    public ResponseEntity<?> deleteFollowUpSession(@PathVariable Long sessionId,
//                                                   @RequestBody FollowUpDeleteRequest request) throws NotFoundException {
//        treatmentSessionService.deleteFollowUpSession(sessionId, request);
//        return ResponseEntity.ok(new ResponseFormat<>(
//                HttpStatus.OK.value(),
//                "DELETE_SUCCESS",
//                "Xóa lịch tái khám thành công",
//                null
//        ));
//    }





}
