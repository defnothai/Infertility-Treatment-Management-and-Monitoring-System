package com.fuhcm.swp391.be.itmms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuhcm.swp391.be.itmms.config.security.PasswordEncoder;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.dto.response.LoginResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Role;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        String token = jwtService.generateJWT(email);
        Account account = null;
        Optional<Account> accountOpt = accountRepo.findByEmail(email);
        if(accountOpt.isPresent()) {
            account = accountOpt.get();
        }
        if(account != null) {
            List<Role> role = account.getRoles();
            System.out.println("Registed");
        } else {
            account = new Account();
            account.setEmail(email);
            account.setPassword(passwordEncoder.bCryptPasswordEncoder().encode("0123456789"));
            account.setCreatedAt(LocalDateTime.now());
            account.setFullName(name);
            Role roleUser = roleRepo.findByRoleName(AccountRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            System.out.println("ROLE = " + roleUser);
            account.setRoles(List.of(roleUser));
            account.setStatus(AccountStatus.ENABLED);
            account.setGender(Gender.MALE);
            accountService.register(account);
            System.out.println("Registed successfully");
        }

        List<AccountRole> roleNames = account.getRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());



        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("email", email);
        responseBody.put("name", name);
        responseBody.put("roles", roleNames);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
