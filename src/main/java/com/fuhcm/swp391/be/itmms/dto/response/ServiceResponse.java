package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.AccountDTO;
import com.fuhcm.swp391.be.itmms.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

    private Long id;
    private String serviceName;
    private String subTitle;
    private double price;
    private String summary;
    private String imgUrl;
    private String slug;
    private ServiceStatus status;
    private ManagerInfo managerInfo;

}
