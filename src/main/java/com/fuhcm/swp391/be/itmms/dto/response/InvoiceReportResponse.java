package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceReportResponse {

    private LocalDate date;
    private double depositAmount;
    private double serviceAmount;
    private double totalAmount;
}
