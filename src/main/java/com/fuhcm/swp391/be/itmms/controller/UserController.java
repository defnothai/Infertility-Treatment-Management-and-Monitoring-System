package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.PatientInfoDetails;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.UserService;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/patients/{accountId}/patient-details")
    public ResponseEntity getPatientInfoDetails(@PathVariable("accountId") Long accountId) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                    "FETCH_DATA_SUCCESS",
                                                    "Lấy thông tin chi tiết bệnh nhân thành công",
                                                    userService.getPatientInfoDetails(accountId)));
    }

    @PutMapping("/api/patients/{accountId}/patient-details")
    public ResponseEntity updatePatientInfoDetails(@Valid @RequestBody PatientInfoDetails patientInfoDetails) throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                        "UPDATED_INFO_SUCCESS",
                                                        "Cập nhật thông tin bệnh nhân thành công",
                                                        userService.updatePatientInfo(patientInfoDetails)));
    }





}
