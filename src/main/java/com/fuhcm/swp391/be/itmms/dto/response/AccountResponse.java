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
    private Long createdBy;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.fullName = account.getFullName();
        this.email = account.getEmail();
        this.createdAt = account.getCreatedAt();
        this.status = account.getStatus();
        this.phoneNumber = account.getPhoneNumber();
        this.gender = account.getGender();
        if(account.getCreatedBy() != null){
            this.createdBy = account.getCreatedBy().getId();
        } else {
            this.createdBy = null;
        }
    }


    public AccountResponse(Long id, String fullName){
        this.id = id;
        this.fullName = fullName;
    }

}
