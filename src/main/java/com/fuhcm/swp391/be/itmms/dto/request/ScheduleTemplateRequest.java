package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ScheduleTemplateRequest {

    private Long accountId;
    private Long shiftId;
    private DayOfWeek dayOfWeek;
    private int maxCapacity;
    private int roomNumber;

}
