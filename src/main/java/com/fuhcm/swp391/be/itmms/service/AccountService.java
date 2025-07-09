package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.dto.PatientInfo;
import com.fuhcm.swp391.be.itmms.dto.response.AccountBasic;
import com.fuhcm.swp391.be.itmms.dto.response.ProfileResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Role;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleService roleService;


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
    public Account updateAccount(Account account) {return accountRepo.save(account);}

    public List<PatientInfo> getPatientInfo() throws NotFoundException {
        Role userRole = roleService.findByRoleName(AccountRole.ROLE_USER);
        List<Account> patients = accountRepo.findByRoles(userRole);

        if (patients.isEmpty()) {
            throw new NotFoundException("Danh sách bệnh nhân trống");
        }

        return patients.stream()
                .map(acc -> modelMapper.map(acc, PatientInfo.class))
                .collect(Collectors.toList());
    }

    public Account findById(Long id) throws NotFoundException {
        return accountRepo.findById(id).orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
    }

    public List<PatientInfo> searchPatientByPhoneNumber(String phoneNumber) throws NotFoundException {
        List<Account> accounts = accountRepo.findByPhoneNumberContaining(phoneNumber);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bệnh nhân");
        }
        return accounts.stream()
                .map(account -> modelMapper.map(account, PatientInfo.class))
                .collect(Collectors.toList());
    }

    public List<PatientInfo> searchPatientByEmail(String email) throws NotFoundException {
        List<Account> accounts = accountRepo.findByEmailContaining(email);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bệnh nhân");
        }
        return accounts.stream()
                .map(account -> modelMapper.map(account, PatientInfo.class))
                .collect(Collectors.toList());
    }

    public List<AccountBasic> getManagerAccount() {
        return accountRepo
                .findByRoles(roleService.findByRoleName(AccountRole.ROLE_MANAGER))
                .stream()
                .map(account -> modelMapper.map(account, AccountBasic.class))
                .collect(Collectors.toList());
    }

    public List<AccountBasic> getDoctorAccounts() {
        List<Account> doctors = accountRepo.findByRoles(roleService.findByRoleName(AccountRole.ROLE_DOCTOR));
        return doctors.stream()
                .map(AccountBasic::new)
                .collect(Collectors.toList());
    }

    public ProfileResponse getUserProfile(Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        if(account == null){
            return null;
        } else {
            ProfileResponse profileResponse = new ProfileResponse();
            User user = account.getUser();
            profileResponse.setUserName(account.getFullName());
            profileResponse.setDateOfBirth(user.getDob());
            profileResponse.setGender(account.getGender());
            profileResponse.setIdentityNumber((user.getIdentityNumber()));
            profileResponse.setNationality(user.getNationality());
            profileResponse.setInsuranceNumber(user.getInsuranceNumber());
            profileResponse.setAddress(user.getAddress());
            return profileResponse;
        }
    }
}
