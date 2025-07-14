package com.fuhcm.swp391.be.itmms.dto;


import com.fuhcm.swp391.be.itmms.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientInfoDetails {

    private PatientInfo patientInfo;
    private User user;

}
