package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordAccessRepository extends JpaRepository<MedicalRecordAccess, Long> {

    Optional<MedicalRecordAccess> findFirstByMedicalRecord_IdAndRoleAndDayEndIsNullOrderByDayStartDesc(
            Long medicalRecordId,
            AccessRole role
    );

    Optional<MedicalRecordAccess> findByMedicalRecordAndRole(MedicalRecord medicalRecord, AccessRole accessRole);

    List<MedicalRecordAccess> findByMedicalRecord_Id(Long medicalRecordId);

    boolean existsByMedicalRecordAndGrantedTo(MedicalRecord medicalRecord, Account grantedTo);

    boolean existsByMedicalRecordAndRole(MedicalRecord medicalRecord, AccessRole accessRole);
}
