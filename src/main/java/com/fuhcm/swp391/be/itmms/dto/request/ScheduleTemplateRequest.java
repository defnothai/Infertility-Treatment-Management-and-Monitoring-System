package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ScheduleTemplateRequest {

//    private Long accountId;
    @NotNull(message = "Day of week không được để trống")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Shift ID không được để trống")
    private Long shiftId;

    @NotNull(message = "Max doctor không được để trống")
    private int maxDoctors;

    @NotNull(message = "Max staff không được để trống")
    private int maxStaffs;
    //private int roomNumber;

}
