package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerInfo {

    private String fullName;
    private String email;
    private String position;

}
