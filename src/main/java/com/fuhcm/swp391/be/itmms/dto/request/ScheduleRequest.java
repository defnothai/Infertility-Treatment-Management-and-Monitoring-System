package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRequest {

    private LocalDate startDate;
    private LocalDate endDate;

}
