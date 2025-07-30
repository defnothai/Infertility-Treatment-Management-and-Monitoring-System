package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.service.Service;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByStatusNot(ServiceStatus serviceStatus);

    List<Service> findByAccount(Account currentAccount);

    Service findByPlans(List<TreatmentPlan> plans);
}
