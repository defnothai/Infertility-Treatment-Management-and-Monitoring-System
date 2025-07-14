package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UltrasoundResponse {

    private Long id;
    private LocalDate date;
    private String result;
    private List<String> imgUrls;
}
