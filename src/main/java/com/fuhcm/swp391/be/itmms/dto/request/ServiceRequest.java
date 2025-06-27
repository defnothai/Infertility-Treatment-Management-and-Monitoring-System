package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.AccountDTO;
import com.fuhcm.swp391.be.itmms.dto.response.ManagerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    private ManagerInfo managerInfo;

}
