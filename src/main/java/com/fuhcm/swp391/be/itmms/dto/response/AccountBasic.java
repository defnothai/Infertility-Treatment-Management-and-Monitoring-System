package com.fuhcm.swp391.be.itmms.dto.response;


import com.fuhcm.swp391.be.itmms.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBasic {

    private String fullName;
    private String email;

    public AccountBasic(Account account) {
        this.fullName = account.getFullName();
        this.email = account.getEmail();
    }
}
