package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface TreatmentSessionRepository extends JpaRepository<TreatmentSession, Long> {
    List<TreatmentSession> findByProgress_IdAndIsActive(Long progressId, boolean isActive);

    Optional<TreatmentSession> findByIdAndIsActiveTrue(Long sessionId);
}
