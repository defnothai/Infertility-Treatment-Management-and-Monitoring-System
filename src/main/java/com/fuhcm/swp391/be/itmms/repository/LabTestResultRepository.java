package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultForStaffResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface LabTestResultRepository extends JpaRepository<LabTestResult, Long> {

    List<LabTestResult> findByLabTestTypeAndMedicalRecord_Id(LabTestResultType labTestType, Long medicalRecordId);

    List<LabTestResult> findBySession_Id(Long sessionId);

    @Query(value = """
    SELECT a.Id
    FROM Account a
    JOIN AccountRoles ar ON a.Id = ar.userId
    JOIN Role r ON r.Id = ar.roleId
    JOIN Schedule s ON a.Id = s.Assigned
    JOIN Shift sh ON sh.Id = s.ShiftID
    LEFT JOIN LabTestResult ltr ON ltr.MadeBy = a.Id
        AND ltr.TestDate = :date
        AND ltr.Status = 'PROCESSING'
    WHERE r.RoleName = 'ROLE_STAFF'
      AND s.Status = 'WORKING'
      AND CAST(:currentTime AS time) BETWEEN sh.StartTime AND sh.EndTime
    GROUP BY a.Id, a.Email
    ORDER BY COUNT(ltr.Id) ASC
""", nativeQuery = true)
    List<Long> findLeastBusyAccountIdByDateAndShift(
            @Param("date") LocalDate date,
            @Param("currentTime") String currentTimeStr
    );

    @Query("""
    SELECT ltr
    FROM LabTestResult ltr
    JOIN ltr.medicalRecord mr
    JOIN mr.user u
    JOIN u.account a
    WHERE (:phoneNumber IS NULL OR a.phoneNumber LIKE %:phoneNumber%)
      AND (:fullName IS NULL OR a.fullName LIKE %:fullName%)
      AND (:testDate IS NULL OR ltr.testDate = :testDate)
""")
    List<LabTestResult> searchByFilters(@Param("phoneNumber") String phoneNumber,
                                        @Param("fullName") String fullName,
                                        @Param("testDate") LocalDate testDate);

    List<LabTestResult> findByAccount_Id(Long staffId);


    // lấy các cụm xn của ngày đó để tạo thành 1 bản ghi
//    @Query("""
//    SELECT new com.fuhcm.swp391.be.itmms.dto.response.LabTestResultForStaffResponse(
//        a.fullName,
//        a.phoneNumber,
//        lr.testDate
//    )
//    FROM LabTestResult lr
//    JOIN lr.medicalRecord mr
//    JOIN mr.user u
//    JOIN u.account a
//    WHERE lr.status = 'UNPAID'
//    GROUP BY a.fullName, a.phoneNumber, lr.testDate
//""")
//    List<LabTestResultForStaffResponse> findUnpaidTestRequestsGrouped();



}
