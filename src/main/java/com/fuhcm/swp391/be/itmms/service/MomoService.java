package com.fuhcm.swp391.be.itmms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MomoService {

    @Value("${momo.partner-code}")
    private String PARTNER_CODE;
    @Value("${momo.access-key}")
    private String ACCESS_KEY;
    @Value("${momo.secret-key}")
    private String SECRET_KEY;
    @Value("${momo.return-url}")
    private String REDIRECT_URL;
    @Value("${momo.ipn-url}")
    private String IPN_URL;
    @Value("${momo.request-type}")
    private String REQUEST_TYPE;

}
