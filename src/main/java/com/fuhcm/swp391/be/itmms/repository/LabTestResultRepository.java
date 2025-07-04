package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestResultRepository extends JpaRepository<LabTestResult, Long> {

    List<LabTestResult> findByLabTestTypeAndMedicalRecord_Id(LabTestResultType labTestType, Long medicalRecordId);

}
