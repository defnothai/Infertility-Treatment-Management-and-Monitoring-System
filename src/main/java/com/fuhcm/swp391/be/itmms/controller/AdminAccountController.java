package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.dto.request.AccountCreateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AccountResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/accounts/manage")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllAccounts(){
        List<AccountResponse> accountResponseList = accountService.getAllAccounts();
        if(accountResponseList.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true, "Không có tài khoản nào", accountResponseList));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách tài khoản thành công", accountResponseList));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createNewAccount(@Valid @RequestBody AccountCreateRequest request){
        Account account = accountService.createNewAccount(request);
        if(account != null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo tài khoản thành công", account));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo tài khoản thất bại", null));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAccount(@Valid @PathVariable long id){
        boolean success = accountService.deleteAccount(id);
        if(success){
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa tài khoản thành công", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa tài khoản thất bại", null));
    }
}
