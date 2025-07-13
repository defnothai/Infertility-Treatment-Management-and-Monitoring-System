package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccountCreateRequest {

    @NotBlank(message = "Tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự")
    private String password;

    @NotNull(message = "Status không được để trống")
    private AccountStatus status;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotNull(message = "Gender không được để trống")
    private Gender gender;

    @NotNull(message = "Role không được để trống")
    private AccountRole roles;
}
