package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.AccountResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ProfileResponse;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập hoặc token không hợp lệ", null));
        }
        ProfileResponse profileResponse = accountService.getProfile(authentication);
        if(profileResponse == null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy profile không thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy profile thành công", profileResponse));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/appointments/available-doctors")
    public ResponseEntity<ApiResponse<?>> getAvailableDoctors(Authentication authentication) {
        Set<AccountResponse> availableDoctors = accountService.getAvailableDoctors();
        if(availableDoctors == null ||  availableDoctors.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách doctor thất bại", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách doctor thành công",  availableDoctors));
    }
}
