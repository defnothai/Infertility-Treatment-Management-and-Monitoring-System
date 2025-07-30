package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.InvoiceStatus;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceResponse {
    private Long id;
    private LocalDate date;
    private double discount;
    private double finalAmount;
    private String status;
    private String notes;
    private String type;

    public InvoiceResponse(Invoice invoice){
        this.id = invoice.getId();
        this.date = invoice.getDate();
        this.discount = invoice.getDiscount();
        this.finalAmount = invoice.getFinalAmount();
        this.status = invoice.getStatus().toString();
        this.notes = invoice.getNotes();
        this.type = invoice.getType();
    }
}
