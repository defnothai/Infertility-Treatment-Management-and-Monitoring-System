package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.AccountResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

//    public List<AccountResponse> getAllAccounts() {
//        List<Account> accounts = accountRepo.findAll();
//
//        return accounts.stream()
//                .map(acc -> new AccountResponse(
//                        acc.getId(),
//                        acc.getFullName(),
//                        acc.getPassword(),
//                        acc.getCreatedAt(),
//                        acc.getStatus(),
//                        acc.getEmail(),
//                        acc.getGender(),
//                        acc.getPhoneNumber()
//                ))
//                .collect(Collectors.toList());
//    }

    public void register(Account account) {
        accountRepo.save(account);
    }

}
