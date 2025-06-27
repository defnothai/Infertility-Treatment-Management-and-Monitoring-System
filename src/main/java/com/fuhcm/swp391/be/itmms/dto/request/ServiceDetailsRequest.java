package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDetailsRequest {

    private String concept;
    private String conceptImgUrl;
    private String condition;
    private String assignment;
    private String unAssignment;
    private String procedureDetails;
    private String procedureDetailsImgUrl;
    private String successRate;
    private String experience;
    private String risk;
    private String hospitalProcedure;
    private String hospitalProcedureImgUrl;
}
