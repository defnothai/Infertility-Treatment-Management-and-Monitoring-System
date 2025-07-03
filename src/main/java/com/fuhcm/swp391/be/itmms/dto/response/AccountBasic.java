package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBasic {

    private String fullName;
    private String email;

}
