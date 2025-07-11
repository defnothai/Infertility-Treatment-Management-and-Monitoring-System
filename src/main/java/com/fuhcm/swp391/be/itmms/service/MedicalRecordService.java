package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.UpdateDiagnosisSymptom;
import com.fuhcm.swp391.be.itmms.dto.response.MedicalRecordResponse;
import com.fuhcm.swp391.be.itmms.dto.response.UserMedicalRecordResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import com.fuhcm.swp391.be.itmms.repository.DoctorRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordAccessRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import com.fuhcm.swp391.be.itmms.repository.UserRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final MedicalRecordAccessRepository medicalRecordAccessRepository;
    private final AuthenticationService authenticationService;
    private final AccountService accountService;
    private final LabTestResultService labTestResultService;
    private final ModelMapper modelMapper;
    private final UltrasoundService ultrasoundService;
    private final DoctorRepository doctorRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                UserRepository userRepository,
                                MedicalRecordAccessRepository medicalRecordAccessRepository,
                                AuthenticationService authenticationService,
                                AccountService accountService,
                                @Lazy LabTestResultService labTestResultService,
                                ModelMapper modelMapper,
                                UltrasoundService ultrasoundService,
                                DoctorRepository doctorRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userRepository = userRepository;
        this.medicalRecordAccessRepository = medicalRecordAccessRepository;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.labTestResultService = labTestResultService;
        this.modelMapper = modelMapper;
        this.ultrasoundService = ultrasoundService;
        this.doctorRepository = doctorRepository;
    }

    public MedicalRecord findById(Long id) throws NotFoundException {
        return medicalRecordRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Hồ sơ không tồn tại"));
    }

    @Transactional
    public MedicalRecordResponse getMedicalRecord(Long accountId) throws NotFoundException {
        Account account = accountService.findById(accountId);
        User user = userRepository.findByAccount(account).orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin bệnh nhân"));

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
        response.setInitUltrasounds(ultrasoundService.getInitialUltrasoundsByMedicalRecordId(medicalRecord.getId()));
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

    public UserMedicalRecordResponse getUserMedicalRecord() throws NotFoundException {
        Account account = authenticationService.getCurrentAccount();
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("Bạn chưa có hồ sơ bệnh án"));
        if (user == null) {
            throw new NotFoundException("Bạn chưa có hồ sơ bệnh án");
        }
        MedicalRecord medicalRecord = medicalRecordRepository.findByUser(user);
        if (medicalRecord == null) {
            throw new NotFoundException("Bạn chưa có hồ sơ bệnh án");
        }
        List<MedicalRecordAccess> access = medicalRecord
                .getMedicalRecordAccess()
                .stream()
                .filter(medicalRecordAccess -> medicalRecordAccess.getDayStart().equals(medicalRecord.getCreatedAt()) &&
                        medicalRecordAccess.getDayEnd() == null)
                .toList();
        if (access.isEmpty()) {
            throw new NotFoundException("Hồ sơ bệnh án đang xử lý");
        }
        Account doctorAccount = access.getFirst().getAccount();
        Doctor doctor = doctorRepository.findByAccount(doctorAccount);
        UserMedicalRecordResponse response = new UserMedicalRecordResponse();
        response.setId(medicalRecord.getId());
        response.setDoctorFullName(doctorAccount.getFullName());
        response.setDoctorEmail(doctorAccount.getEmail());
        response.setDoctorImgUrl(doctor.getImgUrl());
        response.setDoctorPosition(doctor.getPosition());
        response.setDiagnosis(medicalRecord.getDiagnosis());
        response.setSymptoms(medicalRecord.getSymptoms());
        response.setInitLabTestResults(labTestResultService.getInitLabTestResults(medicalRecord.getId()));
        response.setInitUltrasounds(ultrasoundService.getInitialUltrasoundsByMedicalRecordId(medicalRecord.getId()));
        return response;
    }



}
