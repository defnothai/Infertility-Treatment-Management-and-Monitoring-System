package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordAccessService {

    private final AuthenticationService authenticationService;

    public MedicalRecordAccess isAccessedToMedicalRecord(MedicalRecord medicalRecord) {
        Account currentAccount = authenticationService.getCurrentAccount();
        List<MedicalRecordAccess> accesses = medicalRecord.getMedicalRecordAccess();
        for (MedicalRecordAccess access : accesses) {
            if (access.getGrantedTo().getId().equals(currentAccount.getId()) &&
                    (access.getDayEnd() == null || access.getDayEnd().isAfter(LocalDate.now()) || access.getDayEnd().isEqual(LocalDate.now())) &&
                    (access.getDayStart().isBefore(LocalDate.now()) || access.getDayStart().isEqual(LocalDate.now()))) {
                return access;
            }
        }
        return null;
    }

    public boolean canView(MedicalRecord medicalRecord) {
        return isAccessedToMedicalRecord(medicalRecord) != null;
    }

    public boolean canUpdate(MedicalRecord medicalRecord) {
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && (access.getLevel() == PermissionLevel.EDIT || access.getLevel() == PermissionLevel.FULL_ACCESS);
    }

    public boolean canCreate(MedicalRecord medicalRecord) {
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && (access.getLevel() == PermissionLevel.EDIT || access.getLevel() == PermissionLevel.FULL_ACCESS);
    }

    public boolean canDelete(MedicalRecord medicalRecord) {
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && access.getLevel() == PermissionLevel.FULL_ACCESS;
    }

}
