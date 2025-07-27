package com.fuhcm.swp391.be.itmms.config;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ShiftRepository shiftRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initRole();
        entityManager.flush();
        initShift();
        initAccount();
    }

    public void initAccount() {
        if (!accountRepository.findAll().isEmpty()) return;

        Role userRole = roleRepository.findByRoleName(AccountRole.ROLE_USER).orElseThrow();
        Role doctorRole = roleRepository.findByRoleName(AccountRole.ROLE_DOCTOR).orElseThrow();
        Role adminRole = roleRepository.findByRoleName(AccountRole.ROLE_ADMIN).orElseThrow();
        Role managerRole = roleRepository.findByRoleName(AccountRole.ROLE_MANAGER).orElseThrow();
        Role staffRole = roleRepository.findByRoleName(AccountRole.ROLE_STAFF).orElseThrow();

        // Admin
        Account adminAccount = new Account(
                "Trần Hoàng Nam",
                "admin@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS", // 12345678
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0345634668", Gender.MALE,
                null, null,
                new ArrayList<>(List.of(adminRole))
        );
        accountRepository.save(adminAccount);

        // Manager
        Account managerAccount = new Account(
                "Vũ Phong Ba",
                "manager@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0345634669", Gender.MALE,
                adminAccount, null,
                new ArrayList<>(List.of(managerRole))
        );
        accountRepository.save(managerAccount);

        // User
        Account userAccount = new Account(
                "Đào Hồng Hải",
                "daohonghai956@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0366250704", Gender.MALE,
                null, null,
                new ArrayList<>(List.of(userRole))
        );
        accountRepository.save(userAccount);
        userAccount.setCreatedBy(userAccount);
        accountRepository.save(userAccount);

        User user = new User(
                "Quận 12, TPHCM",
                LocalDate.of(2004, 7, 25),
                "678967896789",
                "VietNam",
                "123456789",
                userAccount
        );
        userRepository.save(user);

        // Doctor
        Account doctorAccount = new Account(
                "Nguyễn Hữu Thịnh",
                "doctor@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0345524678", Gender.MALE,
                adminAccount, null,
                new ArrayList<>(List.of(doctorRole))
        );
        accountRepository.save(doctorAccount);

        Doctor doctor = new Doctor(
                "Chuyên khoa hiếm muộn",
                "Bác sĩ hiếm muộn",
                EmploymentStatus.ACTIVE,
                "Bác sĩ với kinh nghiệm 20 năm trong nghề",
                "Luôn tận tâm với nghề, thành công nhiều với những ca hiếm muộn",
                "https://res.cloudinary.com/dcwg1jda0/image/upload/v1752325202/d2460625-181d-4d88-96ab-0efafbb6417a_doctor1.jpg",
                doctorAccount
        );
        doctorRepository.save(doctor);

        // Staff
        Account staffAccount = new Account(
                "Nguyễn Xuân Bình",
                "staff@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0342624678", Gender.MALE,
                adminAccount, null,
                new ArrayList<>(List.of(staffRole))
        );
        accountRepository.save(staffAccount);

        Staff staff = new Staff(staffAccount, EmploymentStatus.ACTIVE, LocalDate.of(2010, 1, 1));
        staffRepository.save(staff);
    }

    public void initRole() {
        if (!roleRepository.findAll().isEmpty()) return;

        roleRepository.saveAll(List.of(
                new Role(AccountRole.ROLE_GUEST),
                new Role(AccountRole.ROLE_USER),
                new Role(AccountRole.ROLE_STAFF),
                new Role(AccountRole.ROLE_ADMIN),
                new Role(AccountRole.ROLE_MANAGER),
                new Role(AccountRole.ROLE_DOCTOR)
        ));
    }

    public void initShift() {
        if (!shiftRepository.findAll().isEmpty()) return;

        shiftRepository.saveAll(List.of(
                new Shift(null, "Sáng", LocalTime.of(0, 0), LocalTime.of(12, 0), null, null),
                new Shift(null, "Chiều", LocalTime.of(13, 0), LocalTime.of(17, 0), null, null),
                new Shift(null, "Tối", LocalTime.of(17, 0), LocalTime.of(23, 0), null, null)
        ));
    }
}
