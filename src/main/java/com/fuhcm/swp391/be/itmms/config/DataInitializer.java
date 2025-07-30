package com.fuhcm.swp391.be.itmms.config;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.entity.prescription.Medication;
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
    private final MedicationRepository medicationRepository;
    private final LabTestRepository labTestRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initMedication();
        initLabTest();
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

        // User ///////////////////////////////////////////////////////////////////////////////////
        // user 1:
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

        // user 2:
        Account userAccount2 = new Account(
                "Nguyễn Nữ Quỳnh Như",
                "nguyennuquynhnhu13@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0366280815", Gender.FEMALE,
                null, null,
                new ArrayList<>(List.of(userRole))
        );
        accountRepository.save(userAccount2);
        userAccount2.setCreatedBy(userAccount2);
        accountRepository.save(userAccount2);

        User user2 = new User(
                "Bình Thạnh, TPHCM",
                LocalDate.of(1995, 8, 10),
                "121212121212",
                "VietNam",
                "123456708",
                userAccount2
        );
        userRepository.save(user2);

        // user 3:
        Account userAccount3 = new Account(
                "Mai Thành Công",
                "wynnhu1311@gmail.com",
                "$2a$12$QiyWhK/jpzaJsR0tNkZ05.CxOYyp923BEMcDCs1R5Zxg1ZeXyI0iS",
                LocalDateTime.now(),
                AccountStatus.ENABLED,
                "0366250815", Gender.MALE,
                null, null,
                new ArrayList<>(List.of(userRole))
        );
        accountRepository.save(userAccount3);
        userAccount3.setCreatedBy(userAccount3);
        accountRepository.save(userAccount3);

        User user3 = new User(
                "Biên Hòa, Đồng Nai",
                LocalDate.of(1990, 1, 10),
                "1234454374",
                "VietNam",
                "1234567663458",
                userAccount3
        );
        userRepository.save(user3);

        // *** done User ///////////////////////////////////////////////////////////////////////////////////

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

    public void initMedication() {
        if (!medicationRepository.findAll().isEmpty()) {
            return;
        }
        medicationRepository.saveAll(List.of(
                new Medication(null, "Clomiphene Citrate", "Kích thích rụng trứng ở nữ giới", "Viên nén", "50mg", "viên", "Ferring", null),
                new Medication(null, "Letrozole", "Hỗ trợ kích thích phóng noãn trong điều trị vô sinh", "Viên nén", "2.5mg", "viên", "Sun Pharma", null),
                new Medication(null, "HCG (Human Chorionic Gonadotropin)", "Kích thích rụng trứng và duy trì hoàng thể", "Ống tiêm", "5000 IU", "ống", "Merck", null),
                new Medication(null, "Progesterone", "Hỗ trợ nội tiết giai đoạn hoàng thể và mang thai", "Viên đặt âm đạo", "200mg", "viên", "Abbott", null),
                new Medication(null, "Folic Acid", "Hỗ trợ phát triển thai, giảm nguy cơ dị tật", "Viên nén", "400mcg", "viên", "Merck", null),
                new Medication(null, "Doxycycline", "Kháng sinh điều trị nhiễm khuẩn đường sinh sản", "Viên nang", "100mg", "viên", "Pfizer", null),
                new Medication(null, "Metformin", "Hỗ trợ điều trị buồng trứng đa nang (PCOS)", "Viên nén", "500mg", "viên", "GSK", null),
                new Medication(null, "Gonadotropin (FSH)", "Kích thích phát triển nang trứng", "Ống tiêm", "75 IU", "ống", "Gonal-F", null),
                new Medication(null, "GnRH Agonist", "Ngăn ngừa rụng trứng sớm trong IVF", "Ống tiêm", "0.1mg", "ống", "Lucrin", null),
                new Medication(null, "Estradiol", "Hỗ trợ phát triển nội mạc tử cung", "Viên uống", "2mg", "viên", "Bayer", null),
                new Medication(null, "Cabergoline", "Giảm prolactin máu cao – nguyên nhân gây vô sinh", "Viên nén", "0.5mg", "viên", "Dostinex", null),
                new Medication(null, "L-Arginine", "Tăng lưu thông máu, hỗ trợ nội mạc tử cung", "Viên uống", "1000mg", "viên", "Now Foods", null),
                new Medication(null, "Coenzyme Q10", "Chống oxy hóa, cải thiện chất lượng trứng", "Viên nang", "200mg", "viên", "Puritan's Pride", null),
                new Medication(null, "Vitamin E", "Chống oxy hóa, hỗ trợ niêm mạc tử cung", "Viên nang", "400 IU", "viên", "Nature Made", null),
                new Medication(null, "Zinc", "Cải thiện chất lượng tinh trùng", "Viên nén", "50mg", "viên", "Nature’s Bounty", null),
                new Medication(null, "Vitamin C", "Tăng cường hệ miễn dịch và chất lượng tinh trùng", "Viên nén", "500mg", "viên", "Kirkland", null)
        ));
    }

    // các type xét nghiệm
    public void initLabTest() {
        if (!labTestRepository.findAll().isEmpty()) {
            return;
        }
        labTestRepository.saveAll(List.of(
                new LabTest(null,
                        "Xét nghiệm nội tiết tố nữ",
                        "Đánh giá hormone như FSH, LH, Estradiol,... để xác định khả năng sinh sản của nữ giới.",
                        "Máu",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm AMH",
                        "Xác định dự trữ buồng trứng của phụ nữ thông qua nồng độ AMH trong máu.",
                        "Máu",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Tinh dịch đồ",
                        "Phân tích số lượng, chất lượng, hình dạng và khả năng di động của tinh trùng.",
                        "Tinh dịch",
                        LocalTime.of(1, 30),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm Prolactin",
                        "Kiểm tra nồng độ Prolactin ảnh hưởng đến quá trình rụng trứng.",
                        "Máu",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm Testosterone",
                        "Đánh giá mức testosterone để kiểm tra chức năng sinh sản nam.",
                        "Máu",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Chụp tử cung vòi trứng (HSG)",
                        "Kiểm tra hình dạng tử cung và độ thông của vòi trứng bằng chụp X-quang.",
                        "Chất cản quang + hình ảnh",
                        LocalTime.of(0, 30),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm nhiễm trùng đường sinh dục",
                        "Tầm soát các tác nhân như Chlamydia, Mycoplasma ảnh hưởng đến khả năng sinh sản.",
                        "Dịch âm đạo / niệu đạo",
                        LocalTime.of(1, 30),
                        null
                ),

                // === Các xét nghiệm bổ sung thường dùng trong điều trị hiếm muộn ===
                new LabTest(null,
                        "Xét nghiệm Progesterone",
                        "Đánh giá giai đoạn hoàng thể và khả năng rụng trứng qua nồng độ Progesterone.",
                        "Máu",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm kháng thể kháng tinh trùng",
                        "Kiểm tra sự tồn tại của kháng thể ảnh hưởng đến chức năng tinh trùng.",
                        "Máu / Dịch cổ tử cung",
                        LocalTime.of(1, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm gen AZF",
                        "Phát hiện đột biến vùng AZF trên nhiễm sắc thể Y gây vô sinh nam.",
                        "Máu",
                        LocalTime.of(3, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm NST đồ",
                        "Kiểm tra bất thường nhiễm sắc thể ở nam và nữ trong vô sinh hiếm muộn.",
                        "Máu",
                        LocalTime.of(4, 0),
                        null
                ),
                new LabTest(null,
                        "Xét nghiệm kháng phospholipid",
                        "Kiểm tra nguyên nhân miễn dịch gây sảy thai liên tiếp hoặc hiếm muộn.",
                        "Máu",
                        LocalTime.of(2, 0),
                        null
                ),
                new LabTest(null,
                        "Siêu âm đầu dò âm đạo",
                        "Đánh giá sự phát triển nang trứng và nội mạc tử cung trong chu kỳ.",
                        "Hình ảnh học",
                        LocalTime.of(0, 30),
                        null
                )
        ));
    }

}
