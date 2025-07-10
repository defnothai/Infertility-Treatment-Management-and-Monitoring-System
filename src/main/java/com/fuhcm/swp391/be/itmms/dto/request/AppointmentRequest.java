package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long doctorId;

    @NotNull(message = "Date không được để trống")
    private LocalDate date;

    @NotNull(message = "Time không được để trống")
    private LocalTime time;

    private String message;

    @NotBlank(message = "Name không được để trống")
    private String patientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @NotNull(message = "DOB không được để trống")
    private LocalDate dob;
}
