package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseHomePage {

    private Long id;
    private String serviceName;
    private String subTitle;
    private String urlImg;
    private String slug;
    private ServiceStatus status;


}
