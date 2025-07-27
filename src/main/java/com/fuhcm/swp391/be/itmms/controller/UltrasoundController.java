package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.UltrasoundRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ImageUrlListResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.UltrasoundResponse;
import com.fuhcm.swp391.be.itmms.service.UltrasoundService;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UltrasoundController {

    private final UltrasoundService ultrasoundService;

    @PostMapping("/api/init-ultrasounds")
    public ResponseEntity createInitUltrasound(@Valid @RequestBody UltrasoundRequest request, Authentication authentication) {
        UltrasoundResponse response = ultrasoundService.createInitUltrasound(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(
                        HttpStatus.CREATED.value(),
                        "CREATE_SUCCESS",
                        "Tạo kết quả siêu âm thành công",
                        response
                ));
    }

    @GetMapping("/api/session/{sessionId}/ultrasound")
    public ResponseEntity getUltrasoundsBySessionId(@PathVariable Long sessionId) {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy thành công ảnh siêu âm",
                        ultrasoundService.getBySessionId(sessionId)
                )
        );
    }

    @PutMapping("/api/ultrasounds/{id}")
    public ResponseEntity updateUltrasound(
            @PathVariable Long id,
            @RequestBody UltrasoundRequest request) {
        UltrasoundResponse response = ultrasoundService.updateUltrasound(id, request);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(
                    HttpStatus.CREATED.value(),
                "UPDATE_SUCCESS",
                "Cập nhật kết quả siêu âm thành công",
                    response
        ));}

    @DeleteMapping("/api/ultrasounds/{id}")
    public ResponseEntity<ResponseFormat<Void>> deleteUltrasound(@PathVariable Long id) {
        ultrasoundService.deleteUltrasound(id);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "DELETE_SUCCESS",
                        "Xóa kết quả siêu âm thành công",
                        null
                )
        );
    }

    @PostMapping("/api/treatment-sessions/{sessionId}/follow-up-ultrasound")
    public ResponseEntity<?> createFollowUpUltrasound(
            @PathVariable Long sessionId,
            @Valid @RequestBody UltrasoundRequest request,
            Authentication authentication) throws NotFoundException {

        UltrasoundResponse response = ultrasoundService.createFollowUpUltrasound(sessionId, request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(
                        HttpStatus.CREATED.value(),
                        "CREATE_SUCCESS",
                        "Tạo kết quả siêu âm FOLLOW_UP thành công",
                        response
                ));
    }

    @GetMapping("/api/ultrasounds/images/{id}")
    public ResponseEntity getImageUrls(@PathVariable Long id) throws NotFoundException {
        ImageUrlListResponse response = ultrasoundService.getImageUrlsById(id);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy thành công ảnh siêu âm",
                        response
                )
        );
    }
}
