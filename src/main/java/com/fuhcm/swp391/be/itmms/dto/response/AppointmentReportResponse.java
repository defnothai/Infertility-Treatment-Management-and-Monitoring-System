package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentReportResponse {

    private LocalDate date;
    private int unPaid;
    private int notPaid;
    private int checkin;
    private int unCheckin;
    private int cancelled;
    private int total;

    public void setTotal(){
        this.total = unPaid + notPaid + checkin + unCheckin + cancelled;
    }
}
