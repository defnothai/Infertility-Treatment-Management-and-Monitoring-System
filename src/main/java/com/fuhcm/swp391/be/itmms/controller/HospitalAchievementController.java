package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.HospitalAchievementRequest;
import com.fuhcm.swp391.be.itmms.dto.response.HospitalAchievementResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.HospitalAchievementService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HospitalAchievementController {

    private final HospitalAchievementService service;

    public HospitalAchievementController(HospitalAchievementService service) {
        this.service = service;
    }

    @GetMapping("/api/hospital-achievements")
    public ResponseEntity getActiveAchievements() {
        List<HospitalAchievementResponse> result = service.getActiveAchievements();
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy danh sách thành tích bệnh viện thành công",
                result));
    }

    @PostMapping("/api/manage/hospital-achievements")
    public ResponseEntity createAchievement(@RequestBody HospitalAchievementRequest request) {
        HospitalAchievementResponse response = service.createAchievement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                        "CREATED_ACHIEVEMENT_SUCCESS",
                                        "Tạo thành tự bệnh viện thành công",
                                        response));
    }

    @PutMapping("/api/manage/hospital-achievements/{id}")
    public ResponseEntity<?> updateAchievement(@PathVariable Long id,
                                               @RequestBody HospitalAchievementRequest request) throws NotFoundException {
        HospitalAchievementResponse response = service.updateAchievement(id, request);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "UPDATE_SUCCESS",
                "Cập nhật thành tích bệnh viện thành công",
                response));
    }

    @DeleteMapping("/api/manage/hospital-achievements/{id}")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long id) throws NotFoundException {
        service.deleteAchievement(id);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "DELETE_SUCCESS",
                "Xóa thành tích bệnh viện thành công",
                null));
    }

    @GetMapping("/api/hospital-achievements/{id}")
    public ResponseEntity<?> getAchievementById(@PathVariable Long id) throws NotFoundException {
        HospitalAchievementResponse response = service.getAchievementById(id);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy thông tin thành tích thành công",
                response));
    }

}
