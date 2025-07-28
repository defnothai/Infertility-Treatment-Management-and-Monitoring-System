package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

@Data
public class EmailDetail {

    private String recipient;
    private String subject;
    private String fullName;
    private String link;
    private String password;

}
