package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 8, message = "Mật khẩu mới ít nhất 8 ký tự")
    private String newPassword;
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

}
