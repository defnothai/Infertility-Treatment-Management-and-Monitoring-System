package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRequest {

    @NotNull(message = "Start Date không được để trống")
    private LocalDate startDate;

    @NotNull(message = "End Date không được để trống")
    private LocalDate endDate;

}
