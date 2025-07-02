package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    boolean existsByAccountIdAndShiftIdAndDayOfWeek(Long accountId, int shiftId, DayOfWeek dayOfWeek);
}
