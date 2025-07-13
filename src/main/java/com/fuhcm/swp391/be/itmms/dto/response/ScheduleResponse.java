package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
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

    public ScheduleResponse(Schedule schedule){
        this.id = schedule.getId();
        this.workDate = schedule.getWorkDate();
        this.note = schedule.getNote();
        this.status = schedule.getStatus();
        this.shiftTime = schedule.getShift().getStartTime() + " - " + schedule.getShift().getEndTime();
    }

}
