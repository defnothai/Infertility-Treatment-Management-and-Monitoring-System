package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleResponse {

    private Long id;
    private LocalDate workDate;
    private int roomNumber;
    private int maxCapacity;
    private String note;
    private ScheduleStatus status;
    private String shiftTime;


}
