package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

@Data
public class DoctorAccountResponse {

    private long accountId; // account của doctor
    private String fullName;
    private String expertise;
    private String position;

}
