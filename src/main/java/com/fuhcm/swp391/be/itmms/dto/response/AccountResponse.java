package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
    private AccountStatus status;
    private String phoneNumber;
    private Gender gender;
    private Account createdBy;

}
