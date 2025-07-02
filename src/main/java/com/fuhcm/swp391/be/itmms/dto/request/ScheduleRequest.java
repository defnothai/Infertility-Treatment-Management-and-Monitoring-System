package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRequest {
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
}
