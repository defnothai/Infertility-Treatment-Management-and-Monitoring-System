package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import lombok.Data;

@Data
public class ScheduleTemplateResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
//    private Integer roomNumber;
//    private Integer maxCapacity;
//    private Long accountId;
//    private String accountFullName;
    private Long shiftId;
    private String shiftTime;
    private int maxStaffs;
    private int maxDoctors;

    public ScheduleTemplateResponse(ScheduleTemplate scheduleTemplate) {
        this.id = scheduleTemplate.getId();
        this.dayOfWeek = scheduleTemplate.getDayOfWeek();
        this.maxStaffs = scheduleTemplate.getMaxStaffs();
        this.maxDoctors = scheduleTemplate.getMaxDoctors();
        this.shiftId = scheduleTemplate.getShift().getId();
        this.shiftTime = scheduleTemplate.getShift().getStartTime() + " - " + scheduleTemplate.getShift().getEndTime();
    }
}

