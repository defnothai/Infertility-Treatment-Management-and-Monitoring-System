package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    private String confirmPassword;
    private String token;
}
