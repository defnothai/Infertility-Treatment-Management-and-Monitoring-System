package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentStageProgressRepository extends JpaRepository<TreatmentStageProgress, Long> {
}
