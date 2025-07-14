package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.service.ServiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceDetailsRepository extends JpaRepository<ServiceDetails, Long> {
    Optional<ServiceDetails> findByServiceId(Long serviceId);
}
