package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.HospitalAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalAchievementRepository extends JpaRepository<HospitalAchievement, Long> {

    List<HospitalAchievement> findByIsActiveTrue();
}
