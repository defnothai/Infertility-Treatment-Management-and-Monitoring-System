package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class FollowUpDTO {

    private LocalDate date;
    private LocalTime time;
    private String message;

}
