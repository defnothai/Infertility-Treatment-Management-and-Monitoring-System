package com.fuhcm.swp391.be.itmms.dto;

import com.fuhcm.swp391.be.itmms.entity.Account;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DoctorAvailability {
    private Account doctor;
    private int appointmentCount;

}
