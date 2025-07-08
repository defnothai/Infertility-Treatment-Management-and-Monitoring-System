package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SessionDetailsResponse {

    List<LabTestResultResponse> labTestResults;
    List<UltrasoundResponse> ultrasounds;

}
