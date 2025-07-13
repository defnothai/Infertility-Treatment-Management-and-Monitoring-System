package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByAssignToId(Long id);

    List<Schedule> findByAssignToIdAndWorkDate(Long id, LocalDate date);

    boolean existsByAssignToAndShiftAndWorkDate(Account assignTo, Shift shift, LocalDate workDate);

    List<Schedule> findByAssignToIdAndWorkDateBetween(Long id, LocalDate start, LocalDate end);

    List<Schedule> findByWorkDateBetween(LocalDate start, LocalDate end);

    List<Schedule> findByWorkDate(LocalDate date);

    Schedule findByAssignToIdAndWorkDateAndShiftId(Long doctorId, LocalDate date, int shiftId);

    List<Schedule> findByWorkDateAndStatus(LocalDate workDate, ScheduleStatus status);

    List<Shift> findShiftsByAssignToAndWorkDate(Account assignTo, LocalDate workDate);

    List<Schedule> findByWorkDateBetweenAndAssignToNot(LocalDate fromDate,LocalDate toDate, Account replace);

}
