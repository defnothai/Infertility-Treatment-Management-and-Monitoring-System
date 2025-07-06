//package com.fuhcm.swp391.be.itmms.config;
//
//import com.fuhcm.swp391.be.itmms.constant.AccountRole;
//import com.fuhcm.swp391.be.itmms.entity.Role;
//import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//
//    public DataInitializer(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
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
//    }
//}
