package com.fuhcm.swp391.be.itmms.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServiceDetailsResponse {

    @Lob
    private String concept;

    @Lob
    private String conceptImgUrl;

    @Lob
    private String condition;

    @Lob
    private String assignment;

    @Lob
    private String unAssignment;

    @Lob
    private String procedureDetails;

    @Lob
    private String procedureDetailsImgUrl;

    @Lob
    private String successRate;

    @Lob
    private String experience;

    @Lob
    private String risk;

    @Lob
    private String hospitalProcedure;

    @Lob
    private String hospitalProcedureImgUrl;

    private String slug;
}
