package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Role;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String fullName;
    private String email;
    private AccountStatus status;
    private String phoneNumber;
    private Gender gender;
    private List<Role> roles;
    private String token;

}
