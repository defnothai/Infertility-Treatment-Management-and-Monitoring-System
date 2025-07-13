//package com.fuhcm.swp391.be.itmms.config;
//
//import com.fuhcm.swp391.be.itmms.constant.AccountRole;
//import com.fuhcm.swp391.be.itmms.entity.Role;
//import com.fuhcm.swp391.be.itmms.entity.Shift;
//import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
//import com.fuhcm.swp391.be.itmms.repository.ShiftRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalTime;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//    private final ShiftRepository shiftRepository;
//
//    public DataInitializer(RoleRepository roleRepository, ShiftRepository shiftRepository) {
//        this.roleRepository = roleRepository;
//        this.shiftRepository = shiftRepository;
//    }
//    public void init() {}
//
//    @Override
//    public void run(String... args) throws Exception {
//        Role role1 = new Role(AccountRole.ROLE_GUEST);
//        Role role2 = new Role(AccountRole.ROLE_USER);
//        Role role3 = new Role(AccountRole.ROLE_STAFF);
//        Role role4 = new Role(AccountRole.ROLE_ADMIN);
//        Role role5 = new Role(AccountRole.ROLE_MANAGER);
//        Role role6 = new Role(AccountRole.ROLE_DOCTOR);
//        roleRepository.save(role1);
//        roleRepository.save(role2);
//        roleRepository.save(role3);
//        roleRepository.save(role4);
//        roleRepository.save(role5);
//        roleRepository.save(role6);
//
//        // Insert shifts
//        Shift morning = new Shift(null, "Morning Shift", LocalTime.of(8, 0), LocalTime.of(12, 0), null, null);
//        Shift afternoon = new Shift(null, "Afternoon Shift", LocalTime.of(13, 0), LocalTime.of(17, 0), null, null);
//        Shift evening = new Shift(null, "Evening Shift", LocalTime.of(18, 0), LocalTime.of(22, 0), null, null);
//
//        shiftRepository.save(morning);
//        shiftRepository.save(afternoon);
//        shiftRepository.save(evening);
//    }
//}
