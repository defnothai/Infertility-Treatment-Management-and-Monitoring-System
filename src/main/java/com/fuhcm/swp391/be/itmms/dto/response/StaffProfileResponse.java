package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StaffProfileResponse extends ProfileResponse{

    public StaffProfileResponse(String userName, String email, String phoneNumber) {
        super(userName, email, phoneNumber);
    }
}
