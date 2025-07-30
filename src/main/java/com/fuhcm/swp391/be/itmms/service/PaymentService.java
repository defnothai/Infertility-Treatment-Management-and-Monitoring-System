package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.payment.PaymentConfig;
import com.fuhcm.swp391.be.itmms.config.security.JWTFilter;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.InvoiceStatus;
import com.fuhcm.swp391.be.itmms.dto.PaymentDTO;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.InvoiceRepository;
import com.fuhcm.swp391.be.itmms.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

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

    @Autowired
    private AppointmentRepository  appointmentRepository;


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

    public PaymentDTO vnPayCallback(String responseCode, String txnRef) {
        Invoice invoice = invoiceRepository.findByOrOrderId(txnRef);
        if(invoice == null){
            return new PaymentDTO("400", "Fail to find invoice", null);
        }
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCode(responseCode);
        paymentDTO.setMessage(null);
        Account account = invoice.getOwner();
        Appointment appointment = appointmentRepository.findByUserId(account.getId()).get();
        paymentDTO.setPaymentUrl(jwtService.generateJWT(account.getEmail()));
        invoice.setStatus(InvoiceStatus.PAID);
        appointment.setStatus(AppointmentStatus.UNCHECKED_IN);
        invoiceRepository.save(invoice);
        appointmentRepository.save(appointment);
        return paymentDTO;
    }

    public String createPaymentLink(Long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        String bankCode = "VNBANK";
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf((int)invoice.getFinalAmount() * 100L));
        vnpParamsMap.put("vnp_BankCode", bankCode);
        vnpParamsMap.put("vnp_IpAddr", "0:0:0:0:0:0:0:1");
        invoice.setOrderId(vnpParamsMap.get("vnp_TxnRef"));
        invoiceRepository.save(invoice);
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        System.out.println(vnPayConfig.getVnp_PayUrl() + "?" + queryUrl);
        return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
    }
}
