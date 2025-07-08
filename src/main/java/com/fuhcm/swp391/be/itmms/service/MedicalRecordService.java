package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.UpdateDiagnosisSymptom;
import com.fuhcm.swp391.be.itmms.dto.response.MedicalRecordResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordAccessRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserService userService;
    private final MedicalRecordAccessRepository medicalRecordAccessRepository;
    private final AuthenticationService authenticationService;
    private final AccountService accountService;
    private final LabTestResultService labTestResultService;
    private final ModelMapper modelMapper;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                UserService userService,
                                MedicalRecordAccessRepository medicalRecordAccessRepository,
                                AuthenticationService authenticationService,
                                AccountService accountService,
                                @Lazy LabTestResultService labTestResultService, ModelMapper modelMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userService = userService;
        this.medicalRecordAccessRepository = medicalRecordAccessRepository;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.labTestResultService = labTestResultService;
        this.modelMapper = modelMapper;
    }


    public MedicalRecord findById(Long id) throws NotFoundException {
        return medicalRecordRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Hồ sơ không tồn tại"));
    }

    @Transactional
    public MedicalRecordResponse getMedicalRecord(Long accountId) throws NotFoundException {
        Account account = accountService.findById(accountId);
        User user = userService.findByAccount(account);

        MedicalRecord medicalRecord = medicalRecordRepository.findByUser(user);
        if (medicalRecord == null) {
            medicalRecord = new MedicalRecord();
            medicalRecord.setUser(user);
            medicalRecord.setDiagnosis("");
            medicalRecord.setSymptoms("");
            medicalRecord.setCreatedAt(LocalDate.now());

            Account currentAccount = authenticationService.getCurrentAccount();
            MedicalRecordAccess access = new MedicalRecordAccess();
            access.setAccount(currentAccount);
            access.setMedicalRecord(medicalRecord);
            access.setDayStart(LocalDate.now());
            access.setDayEnd(null);

            access = medicalRecordAccessRepository.save(access);
            medicalRecord.setMedicalRecordAccess(List.of(access));
            medicalRecord = medicalRecordRepository.save(medicalRecord);
        }

        MedicalRecordResponse response = modelMapper.map(medicalRecord, MedicalRecordResponse.class);

        response.setFullName(user.getAccount().getFullName());
        response.setGender(user.getAccount().getGender());
        response.setPhoneNumber(user.getAccount().getPhoneNumber());
        response.setDob(user.getDob());
        response.setAddress(user.getAddress());
        response.setIdentityNumber(user.getIdentityNumber());
        response.setNationality(user.getNationality());
        response.setInsuranceNumber(user.getInsuranceNumber());

        response.setInitLabTestResults(labTestResultService.getInitLabTestResults(medicalRecord.getId()));

        return response;
    }

    @Transactional
    public UpdateDiagnosisSymptom updateDiagnosisAndSymptoms(Long medicalRecordId, UpdateDiagnosisSymptom request) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ bệnh án"));

        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setSymptoms(request.getSymptoms());

        medicalRecordRepository.save(medicalRecord);

        return new UpdateDiagnosisSymptom(
                medicalRecord.getDiagnosis(),
                medicalRecord.getSymptoms()
        );
    }



}
