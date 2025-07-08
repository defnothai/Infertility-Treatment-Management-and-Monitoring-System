package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findById(long id);

    @Query(
            value = "SELECT TOP 1 * FROM Shift " +
                    "WHERE StartTime <= CAST(:startTime AS TIME) AND EndTime >= CAST(:endTime AS TIME)",
            nativeQuery = true
    )
    Optional<Shift> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalTime startTime, LocalTime endTime);
}
