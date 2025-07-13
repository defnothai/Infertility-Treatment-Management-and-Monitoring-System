package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

@Data
public class EmailDetailReminder {

    private String recipient;
    private String subject;
    private String patientName;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String doctorName;
    private String message;
    private String note;

}
