package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceStageRepository extends JpaRepository<ServiceStage, Long> {

    @Query("SELECT MAX(s.stageOrder) " +
            "FROM ServiceStage s WHERE s.service.id = :serviceId AND s.isActive != false")
    Optional<Integer> findMaxStageOrderByServiceId(@Param("serviceId") Long serviceId);

    List<ServiceStage> findByServiceIdAndIsActiveIsTrue(Long serviceId);


}
