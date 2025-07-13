package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Title không được để trống")
    private String title;

    @Lob
    @NotBlank(message = "Description không được để trống")
    private String description;

    @NotNull(message = "Ngày xin nghỉ không được để trống")
    private LocalDate date;

}
