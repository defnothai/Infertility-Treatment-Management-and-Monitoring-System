package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalRecordAccessRequest {
    private long accountId; // id account của doctor
    private LocalDateTime dayStart;
    private LocalDateTime dayEnd;
    private AccessRole role;
    private PermissionLevel level;

}
