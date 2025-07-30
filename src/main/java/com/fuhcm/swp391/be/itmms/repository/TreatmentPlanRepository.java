package com.fuhcm.swp391.be.itmms.repository;


import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {

    List<TreatmentPlan> findByMedicalRecord_Id(Long medicalRecordId);


    TreatmentPlan findByMedicalRecord(MedicalRecord record);

    List<TreatmentPlan> findByStatus(TreatmentPlanStatus status);

}
