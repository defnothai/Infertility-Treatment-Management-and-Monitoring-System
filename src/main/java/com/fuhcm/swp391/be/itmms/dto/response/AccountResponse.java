package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountResponse {
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
}
