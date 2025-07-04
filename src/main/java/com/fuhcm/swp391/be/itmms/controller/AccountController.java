package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.ProfileResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/api/patients")
    public ResponseEntity getPatientList(
            @RequestParam(value = "phone-number", required = false) String phoneNumber,
            @RequestParam(value = "email", required = false) String email
    ) throws NotFoundException {
        if (phoneNumber != null) {
            return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                    "FETCH_DATA_SUCCESS",
                    "Lấy thông tin theo số điện thoại thành công",
                    accountService.searchPatientByPhoneNumber(phoneNumber)));
        } else if (email != null) {
            return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                    "FETCH_DATA_SUCCESS",
                    "Lấy thông tin theo email thành công",
                    accountService.searchPatientByEmail(email)));
        } else {
            return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                    "FETCH_DATA_SUCCESS",
                    "Lấy tất cả bệnh nhân thành công",
                    accountService.getPatientInfo()));
        }
    }

    @GetMapping("/api/user/profile")
    public ResponseEntity getUserProfile(Authentication authentication) throws NotFoundException {
        ProfileResponse profileResponse = accountService.getUserProfile(authentication);
        if(profileResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy thông tin profile thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy thông tin profile thành công",
                profileResponse));
    }
}
