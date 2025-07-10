package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordAccessRepository extends JpaRepository<MedicalRecordAccess, Long> {

    Optional<MedicalRecordAccess> findFirstByMedicalRecord_IdAndRoleAndDayEndIsNullOrderByDayStartDesc(
            Long medicalRecordId,
            AccessRole role
    );


}
