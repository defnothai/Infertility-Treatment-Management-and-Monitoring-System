package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.UltrasoundType;
import com.fuhcm.swp391.be.itmms.entity.Ultrasound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UltrasoundRepository extends JpaRepository<Ultrasound, Long> {

    List<Ultrasound> findByMedicalRecordIdAndTypeAndIsActiveTrue(Long medicalRecordId, UltrasoundType type);

    List<Ultrasound> findBySession_IdAndIsActiveTrue(Long sessionId);
}
