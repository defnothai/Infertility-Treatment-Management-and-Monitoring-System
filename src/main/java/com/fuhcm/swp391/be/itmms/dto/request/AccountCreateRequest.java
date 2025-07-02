package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.Data;

@Data
public class AccountCreateRequest {
    private String fullName;
    private String email;
    private String password;
    private AccountStatus status;
    private String phoneNumber;
    private Gender gender;
    private Long createdBy;
    private AccountRole roles;
}
