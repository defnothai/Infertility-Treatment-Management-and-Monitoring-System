package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.PaymentDTO;
import com.fuhcm.swp391.be.itmms.dto.response.ApiResponse;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/vn-pay")
    public ResponseEntity<ApiResponse<?>> pay(@Valid HttpServletRequest request,@Valid Authentication authentication) {
        PaymentDTO paymentDTO = paymentService.createVnPayPayment(request, authentication);
        if(paymentDTO == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo bill không thành công", null ));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo bill thành công", paymentDTO));
    }
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ApiResponse<?>> payCallbackHandler(@RequestParam("vnp_ResponseCode") String responseCode,
                                                             @RequestParam("vnp_TxnRef") String txnRef) {
        PaymentDTO paymentDTO = paymentService.vnPayCallback(responseCode, txnRef);
        if(paymentDTO == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, null, null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, null, paymentDTO));
    }

    public String createPaymentLink(Long id) {
        return paymentService.createPaymentLink(id);
    }
}
