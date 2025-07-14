package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountReportResponse {

    private LocalDate date;
    private int totalAccount;
    private int enabledAccount;
    private int disabledAccount;
    private int deletedAccount;
}
