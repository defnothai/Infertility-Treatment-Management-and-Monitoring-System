package com.fuhcm.swp391.be.itmms.dto.request;

import lombok.Data;

@Data
public class FollowUpDeleteRequest {

    private String message; // gửi đến bệnh nhân
    private String note;    // lý do hủy

}
