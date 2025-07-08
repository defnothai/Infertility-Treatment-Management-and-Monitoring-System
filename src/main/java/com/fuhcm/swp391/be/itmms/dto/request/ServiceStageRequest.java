package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServiceStageRequest {

    private String name;
    private int duration;

}
