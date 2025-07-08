package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    boolean existsByAccountIdAndShiftIdAndDayOfWeek(Long accountId, int shiftId, DayOfWeek dayOfWeek);
}
