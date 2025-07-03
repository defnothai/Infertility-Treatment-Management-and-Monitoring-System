package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping("/api/staffs")
//    public ResponseEntity getStaffAccount() {
//        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
//                                                        "FETCH_SUCCESS",
//                                                        "Lấy thông tin thành công",
//                                                        accountService.getStaffAccount()));
//    }

}
