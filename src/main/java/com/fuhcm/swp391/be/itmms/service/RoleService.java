package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.entity.Role;
import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByRoleName(AccountRole roleUser) {
        return roleRepository.findByRoleName(roleUser)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò người dùng không xác định"));
    }
}
