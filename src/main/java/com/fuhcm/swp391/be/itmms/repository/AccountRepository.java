package com.fuhcm.swp391.be.itmms.repository;

import com.fuhcm.swp391.be.itmms.dto.PatientInfo;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String gmail);

    List<Account> findByRoles(List<Role> roles);

    List<Account> findByPhoneNumberContaining(String phoneNumber);

    List<Account> findByEmailContaining(String email);

}
