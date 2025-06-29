package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceStageResponse {

    private int id;
    private String name;
    private int stageOrder;
    private int duration;

}

