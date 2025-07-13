package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.response.AccountBasic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {

    private String serviceName;
    private String subTitle;
    private double price;
    private String summary;
    private ServiceStatus status;
    private String imgUrl;
    private AccountBasic managerAccount; // update không cần

}
