package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

@Data
public class EmailDetail {

    private String recipient;
    private String subject;
    private String fullName;
    private String link;
    //
    private String password;
    //
    private String note;
    private String message;
    private String oldDate;
    private String oldTime;
    private String newDate;
    private String newTime;
    private String doctorName;

}
