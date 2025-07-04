package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.PatientInfo;
import com.fuhcm.swp391.be.itmms.dto.PatientInfoDetails;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.repository.UserRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, AccountService accountService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    public User findById(long id) throws NotFoundException {
        return userRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Thông tin người dùng không tồn tại"));
    }

    public User findByAccount(Account account) throws NotFoundException {
        return userRepository
                .findByAccount(account)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết bệnh nhân"));
    }

    public PatientInfoDetails getPatientInfoDetails(Long accountId) throws NotFoundException {
        Account account = accountService.findById(accountId);
        PatientInfo patientInfo = modelMapper.map(account, PatientInfo.class);
        User user = this.findByAccount(account);
        return new PatientInfoDetails(patientInfo, user);
    }

    @Transactional
    public PatientInfoDetails updatePatientInfo(PatientInfoDetails patientInfoDetails) throws NotFoundException {
        // Update Account

        PatientInfo patientInfo = patientInfoDetails.getPatientInfo();
        Account account = accountService.findById(patientInfo.getId());
        account.setFullName(patientInfo.getFullName());
        account.setGender(patientInfo.getGender());
        account.setPhoneNumber(patientInfo.getPhoneNumber());
        account = accountService.updateAccount(account);

        // Update User
        User userRequest = patientInfoDetails.getUser();
        User user = this.findById(userRequest.getId());

        user.setAddress(userRequest.getAddress());
        user.setDob(userRequest.getDob());
        user.setIdentityNumber(userRequest.getIdentityNumber());
        user.setNationality(userRequest.getNationality());
        user.setInsuranceNumber(userRequest.getInsuranceNumber());
        user.setAccount(account);

        user = userRepository.save(user);
        return new PatientInfoDetails(modelMapper.map(account, PatientInfo.class), user);
    }


}
