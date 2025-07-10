package com.fuhcm.swp391.be.itmms.dto;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
public class PaymentDTO {
    private String code;
    private String message;
    private String paymentUrl;

    public PaymentDTO(String code, String message, String paymentUrl) {
        this.code = code;
        this.message = message;
        this.paymentUrl = paymentUrl;
    }
}
