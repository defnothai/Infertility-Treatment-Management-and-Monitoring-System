package com.fuhcm.swp391.be.itmms.constant;

public enum MomoParameter {
    PARTNER_CODE("partnerCode"),
    PARTNER_CLIENT_ID("partnerClientId"),
    CALLBACK_TOKEN("callbackToken"),
    DESCRIPTION("description"),
    ACCESS_KEY("accessKey"),
    REQUEST_ID("requestId"),
    AMOUNT("amount"),
    ORDER_ID("orderId"),
    ORDER_INFO("orderInfo"),
    REQUEST_TYPE("requestType"),
    EXTRA_DATA("extraData"),
    MESSAGE("message"),
    PAY_URL("payUrl"),
    RESULT_CODE("resultCode"),
    REDIRECT_URL("redirectUrl"),
    IPN_URL("ipnUrl"),
    TOKEN("token"),
    TRANS_ID("transId");

    private final String key;

    MomoParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
