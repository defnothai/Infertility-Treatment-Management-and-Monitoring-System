package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndTime(Long id, LocalDate time);

    List<Appointment> findByTime(LocalDate date);

    List<Appointment> findByDoctorIdAndTimeAndStatusNot(Long id, LocalTime time, AppointmentStatus status);

    boolean existsByUserIdAndTime(Long id, LocalDate date);

    Optional<Appointment> findByUserId(Long id);

    Optional<List<Appointment>> findByDoctorId(Long id);

    Optional<Appointment> findByUserIdAndTime(Long id, LocalDate date);
}
