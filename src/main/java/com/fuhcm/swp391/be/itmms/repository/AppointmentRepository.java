package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndTime(Long id, LocalDate time);

    List<Appointment> findByTime(LocalDate date);

    List<Appointment> findByDoctorIdAndTimeAndStatusNot(Long id, LocalDate time, AppointmentStatus status);

    boolean existsByUserIdAndTime(Long id, LocalDate date);

    Optional<Appointment> findByUserId(Long id);

    Optional<List<Appointment>> findByDoctorId(Long id);

    Optional<Appointment> findByUserIdAndTime(Long id, LocalDate date);

    List<Appointment> findByStatusAndCreateAtBefore(AppointmentStatus appointmentStatus, LocalDateTime oneMinuteAgo);

    boolean existsByUserIdAndTimeAndStatusIsNot(Long id, LocalDate date, AppointmentStatus appointmentStatus);

    List<Appointment> findByStatusAndTimeLessThanEqual(AppointmentStatus appointmentStatus, LocalDate now);

    Appointment findTopByUserOrderByCreateAtDesc(Account account);

    Optional<Appointment> findBySessionId(Long id);

    List<Appointment> findByTimeBetween(LocalDate timeAfter, LocalDate timeBefore);
}
