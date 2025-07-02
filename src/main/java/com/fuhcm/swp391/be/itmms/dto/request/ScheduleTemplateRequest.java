package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import lombok.Data;

@Data
public class ScheduleTemplateRequest {
    private DayOfWeek dayOfWeek;
    private int roomNumber;
    private int maxCapacity;
    private Shift shift;
    private Account account;
}
