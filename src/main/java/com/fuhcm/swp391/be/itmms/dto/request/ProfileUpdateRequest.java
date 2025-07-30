package com.fuhcm.swp391.be.itmms.dto.request;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.validation.OnUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, message = "Họ tên ít nhất 3 ký tự")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Không được để trống giới tính")
    private Gender gender;

    @NotBlank(message = "Địa chỉ không được để trống", groups = OnUpdate.class)
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự", groups = OnUpdate.class)
    @Size(min = 10, message = "Địa chỉ ít nhất 10 ký tự" , groups = OnUpdate.class)
    private String address;

    @NotNull(message = "Ngày sinh không được để trống" , groups = OnUpdate.class)
    @Past(message = "Ngày sinh không hợp lệ" , groups = OnUpdate.class)
    @Column(name = "DayOfBirth", nullable = true)
    private LocalDate dob;

    @Size(max = 15, message = "Số CMND/CCCD tối đa 15 ký tự", groups = OnUpdate.class)
    @Size(min = 9, message = "Số CMND/CCCD ít nhất 9 ký tự", groups = OnUpdate.class)
    @Pattern(regexp = "\\d{9,15}", message = "Số CMND/CCCD chỉ được chứa chữ số", groups = OnUpdate.class)
    private String identityNumber;

    @NotBlank(message = "Quốc tịch không được để trống" , groups = OnUpdate.class)
    @Size(max = 15, message = "Quốc tịch tối đa 15 ký tự", groups = OnUpdate.class)
    @Size(min = 2, message = "Quốc tịch ít nhất 2 ký tự", groups = OnUpdate.class)
    private String nationality;

    @Size(min = 0, max = 20, message = "Số BHYT tối đa 20 chữ số", groups = OnUpdate.class)
    @Pattern(regexp = "\\d{0,20}", message = "Số BHYT chỉ được chứa chữ số", groups = OnUpdate.class)
    private String insuranceNumber;



}
