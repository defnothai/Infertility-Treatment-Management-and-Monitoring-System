package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import lombok.Data;

@Data
public class ScheduleTemplateResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
    private Integer roomNumber;
    private Integer maxCapacity;
    private Long accountId;
    private String accountFullName;
    private Long shiftId;
    private String shiftTime;
}

