package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

    private String serviceName;
    private String subTitle;
    private String urlImg;
    private String slug;


}
