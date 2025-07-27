package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

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
        ProfileResponse profile = accountService.getUserProfile(authentication);
        if(profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy thông tin profile thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy thông tin profile thành công",
                profile));
    }

    @GetMapping("/api/manage/doctors")
    public ResponseEntity getDoctorAccounts() {
        List<AccountBasic> doctorAccounts = accountService.getDoctorAccounts();
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách tài khoản bác sĩ thành công",
                        doctorAccounts)
        );
    }

    @GetMapping("/api/manage/doctors/search")
    public ResponseEntity<?> searchDoctors(@RequestParam("keyword") String keyword) throws NotFoundException {
        List<AccountBasic> results = accountService.searchDoctors(keyword);
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "SEARCH_SUCCESS",
                        "Tìm kiếm tài khoản bác sĩ thành công",
                        results)
        );
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/user/appointments/available-doctors")
    public ResponseEntity<ApiResponse<?>> getAvailableDoctors() {
        Set<AccountResponse> availableDoctors = accountService.getAvailableDoctors();
        if(availableDoctors == null ||  availableDoctors.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách doctor thất bại", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách doctor thành công",  availableDoctors));
    }

    @GetMapping("/report")
    public ResponseEntity<?> getAccountReport(@Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
                                              @Valid @RequestParam("toDate") @NotNull LocalDate toDate) throws NotFoundException {
        List<AccountReportResponse> response = accountService.getAccountReport(fromDate, toDate);
        if(response == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy danh sách tài khoản thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách tài khoản thành công",
                        response));
    }

}
