package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FollowUpRequest {

    private LocalDate date;
    private LocalTime time;
    private String message;

}
