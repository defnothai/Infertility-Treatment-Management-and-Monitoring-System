package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.PatientInfo;
import com.fuhcm.swp391.be.itmms.dto.PatientInfoDetails;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.repository.UserRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public PatientInfoDetails getPatientInfoDetails(Long accountId) throws NotFoundException {
        Account account = accountService.findById(accountId);
        PatientInfo patientInfo = modelMapper.map(account, PatientInfo.class);
        User user = userRepository
                .findByAccount(account)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết bệnh nhân"));
        return new PatientInfoDetails(patientInfo, user);
    }
}
