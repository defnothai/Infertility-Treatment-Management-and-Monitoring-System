package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.AccountPrincipal;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.repository.AuthenticationRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final AuthenticationService authenticationService;

    public MyUserDetailsService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new AccountPrincipal(authenticationService.findByEmail(email));
    }
}
