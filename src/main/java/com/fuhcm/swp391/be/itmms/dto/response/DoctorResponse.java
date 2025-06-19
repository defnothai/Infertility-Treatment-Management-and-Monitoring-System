package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {

    private String fullName;
    private String position;
    private String achievement;
    private String imgUrl;
    private String slug;

}
