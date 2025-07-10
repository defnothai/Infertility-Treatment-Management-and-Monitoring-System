package com.fuhcm.swp391.be.itmms.config.payment;

import com.fuhcm.swp391.be.itmms.utils.VNPayUtil;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class PaymentConfig {
    private String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private String vnp_ReturnUrl = "http://localhost:3000/payment-result";
    private String vnp_TmnCode = "7TXS9FV1" ;
    private String secretKey = "1S3XLZFHG1OCYTVUH31VEOSYBQF0GPMI";
    private String vnp_Version = "2.1.0";
    private String vnp_Command = "pay";
    private String orderType = "other";

    public String getVnp_PayUrl() {
        return vnp_PayUrl;
    }

    public String getVnp_ReturnUrl() {
        return vnp_ReturnUrl;
    }

    public String getVnp_TmnCode() {
        return vnp_TmnCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getVnp_Version() {
        return vnp_Version;
    }

    public String getVnp_Command() {
        return vnp_Command;
    }

    public String getOrderType() {
        return orderType;
    }

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef",  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" +  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}
