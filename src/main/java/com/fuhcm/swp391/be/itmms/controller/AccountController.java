package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.DirectPatientDTO;
import com.fuhcm.swp391.be.itmms.dto.request.ProfileUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import com.fuhcm.swp391.be.itmms.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/api/staff/direct-patients")
    public ResponseEntity createPatientAccount(@Valid @RequestBody DirectPatientDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                        "CREATED_SUCCESS",
                        "Tạo tài khoản thành công",
                        accountService.createDirectPatient(request)));
    }

    @GetMapping("/api/staff/direct-patients")
    public ResponseEntity<?> getAllDirectPatientsByStaff() {
        List<DirectPatientDTO> result = accountService.getDirectPatientsByCurrentStaff();
        return ResponseEntity.ok(new ResponseFormat<>(
                HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy danh sách bệnh nhân thành công",
                result
        ));
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

    @PutMapping("/api/user/profile")
    public ResponseEntity updateUserProfile(@Validated(OnUpdate.class) @RequestBody ProfileUpdateRequest request) throws NotFoundException {
        ProfileResponse profile = accountService.updateUserProfile(request);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "UPDATE_SUCCESS",
                "Cập nhật thông tin profile thành công",
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

    @GetMapping("api/accounts/login-info")
    public ResponseEntity getInfoLogin(Authentication authentication) throws NotFoundException {
        AccountBasic response = accountService.getInfoLogin(authentication);
        if(response == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy information thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy information thành công",
                        response));
    }


}
