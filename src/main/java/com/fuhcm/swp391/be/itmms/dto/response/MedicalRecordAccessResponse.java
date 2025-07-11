package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalRecordAccessResponse {

    private Long id;
    private LocalDate dayStart;
    private LocalDate dayEnd;
    private PermissionLevel level;
    private AccessRole role;
    private String grantedBy; // Manager - name
    private String doctorName; // Doctor - name
    private String expertise; // của doctor
    private String position; // của doctor

}
