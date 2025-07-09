package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {


    Doctor findByAccount(Account account);

    @Query("SELECT d FROM Doctor d WHERE d.account.email = :email")
    Optional<Doctor> findByAccountEmail(String email);



}
