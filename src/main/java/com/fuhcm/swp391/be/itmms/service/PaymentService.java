package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.payment.PaymentConfig;
import com.fuhcm.swp391.be.itmms.config.security.JWTFilter;
import com.fuhcm.swp391.be.itmms.constant.InvoiceStatus;
import com.fuhcm.swp391.be.itmms.dto.PaymentDTO;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.InvoiceRepository;
import com.fuhcm.swp391.be.itmms.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentConfig vnPayConfig;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private JWTService jwtService;

    public PaymentDTO createVnPayPayment(HttpServletRequest request, Authentication authentication) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("vnp_BankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        Invoice  invoice = new Invoice();
        invoice.setDate(LocalDate.now());
        invoice.setTotal(amount);
        invoice.setFinalAmount(amount - invoice.getDiscount());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setType("DEPOSIT");
        invoice.setOrderId(vnpParamsMap.get("vnp_TxnRef"));
        invoice.setOwner(jwtService.extractAccount(jwtFilter.getToken(request)));
        invoice.setAccount(null);
        invoice.setTreatmentPlan(null);
        invoice.setInvoicePayments(null);
        invoiceRepository.save(invoice);
        return new PaymentDTO("ok", "success", paymentUrl);
    }

    public PaymentDTO vnPayCallback(String responseCode, String txnRef, String message) {

        Invoice invoice = invoiceRepository.findByOrOrderId(txnRef);
        if(invoice == null){
            return new PaymentDTO("400", "Fail to find invoice", null);
        }
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCode(responseCode);
        paymentDTO.setMessage(message);
        Account account = invoice.getOwner();
        paymentDTO.setPaymentUrl(jwtService.generateJWT(account.getEmail()));
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
        return paymentDTO;
    }
}
