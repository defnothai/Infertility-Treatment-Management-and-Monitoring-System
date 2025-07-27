package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import com.fuhcm.swp391.be.itmms.dto.request.UpdateDiagnosisSymptom;
import com.fuhcm.swp391.be.itmms.dto.response.*;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final MedicalRecordAccessService medicalRecordAccessService;


    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                UserRepository userRepository,
                                MedicalRecordAccessRepository medicalRecordAccessRepository,
                                AuthenticationService authenticationService,
                                AccountService accountService,
                                @Lazy LabTestResultService labTestResultService,
                                ModelMapper modelMapper,
                                UltrasoundService ultrasoundService,
                                DoctorRepository doctorRepository,
                                MedicalRecordAccessService medicalRecordAccessService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userRepository = userRepository;
        this.medicalRecordAccessRepository = medicalRecordAccessRepository;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.labTestResultService = labTestResultService;
        this.modelMapper = modelMapper;
        this.ultrasoundService = ultrasoundService;
        this.doctorRepository = doctorRepository;
        this.medicalRecordAccessService = medicalRecordAccessService;
    }

    public MedicalRecord findById(Long id) throws NotFoundException {
        return medicalRecordRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Hồ sơ không tồn tại"));
    }

    // lấy lịch sử khám bệnh
    public List<MedicalRecordSummaryResponse> getMedicalRecordsByAccountId(Long accountId) {
        List<MedicalRecord> records = medicalRecordRepository.findByUserAccountId(accountId);
        return records.stream().map(MedicalRecordSummaryResponse::new).collect(Collectors.toList());
    }

    // lấy lịch sử khám bệnh cho bệnh nhân
    public List<MedicalRecordSummaryResponse> getMyMedicalRecords() {
        Long accountId = authenticationService.getCurrentAccount().getId();
        List<MedicalRecord> records = medicalRecordRepository.findByUserAccountId(accountId);
        return records.stream().map(MedicalRecordSummaryResponse::new).collect(Collectors.toList());
    }

    // tạo hồ sơ khám trong lịch sử
    public MedicalRecordSummaryResponse createNewMedicalRecord(Long accountId) {
        Account currentAccount = authenticationService.getCurrentAccount();
        MedicalRecord medicalRecord = new MedicalRecord();
        User user = userRepository.findByAccount_Id(accountId);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy được thông tin để tạo hồ sơ");
        }
        medicalRecord.setUser(user);
        medicalRecord.setDiagnosis("");
        medicalRecord.setSymptoms("");
        medicalRecord.setCreatedAt(LocalDate.now());
        medicalRecord = medicalRecordRepository.save(medicalRecord);
        // access
        MedicalRecordAccess access = new MedicalRecordAccess();
        access.setGrantedTo(currentAccount);
        access.setGrantedBy(currentAccount);
        access.setLevel(PermissionLevel.FULL_ACCESS);
        access.setMedicalRecord(medicalRecord);
        access.setDayStart(LocalDateTime.now());
        access.setRole(AccessRole.MAIN_DOCTOR);
        access.setDayEnd(null);
        //
        access = medicalRecordAccessRepository.save(access);
        medicalRecord.setMedicalRecordAccess(List.of(access));

        // dto
        return new MedicalRecordSummaryResponse(medicalRecord);
    }

    // lấy chi tiết hồ sơ khám
    @Transactional
    public MedicalRecordResponse getMedicalRecord(Long recordId) throws NotFoundException {
        Account currentAccount = authenticationService.getCurrentAccount();
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));
        PermissionLevel level = PermissionLevel.FULL_ACCESS;
        if (medicalRecord != null) {
            if (!medicalRecordAccessService.canView(medicalRecord)) {
                throw new NotFoundException("Bạn không thể truy cập vào hồ sơ này");
            }
            MedicalRecordAccess recordAccess = medicalRecord.getMedicalRecordAccess()
                    .stream().filter(medicalRecordAccess -> medicalRecordAccess.getGrantedTo().equals(currentAccount)).findFirst().orElse(null);
            if (recordAccess == null) {
                throw new RuntimeException("Không kiểm tra được khả năng truy cập");
            }
            level = recordAccess.getLevel();
        }
        User user = medicalRecord.getUser();
        if (user == null) {
            throw new RuntimeException("Không tìm thấy thông tin bệnh nhân");
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
        response.setLevel(level);

        response.setInitLabTestResults(labTestResultService.getInitLabTestResults(medicalRecord.getId()));
        response.setInitUltrasounds(ultrasoundService.getInitialUltrasoundsByMedicalRecordId(medicalRecord.getId()));
        return response;
    }

    // điền triệu chứng kết luận ban đầu
    @Transactional
    public UpdateDiagnosisSymptom updateDiagnosisAndSymptoms(Long medicalRecordId, UpdateDiagnosisSymptom request) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ bệnh án"));
        if (!medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể cập nhật hồ sơ này");
        }
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setSymptoms(request.getSymptoms());

        medicalRecordRepository.save(medicalRecord);

        return new UpdateDiagnosisSymptom(
                medicalRecord.getDiagnosis(),
                medicalRecord.getSymptoms()
        );
    }

    public UserMedicalRecordResponse getUserMedicalRecord(Long recordId) throws NotFoundException {
        Account account = authenticationService.getCurrentAccount();
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("Bạn chưa có hồ sơ bệnh án"));
        if (user == null) {
            throw new NotFoundException("Bạn chưa có hồ sơ bệnh án");
        }
        MedicalRecord medicalRecord = medicalRecordRepository
                                        .findById(recordId)
                                        .orElseThrow(
                () -> new NotFoundException("Không tìm thấy hồ sơ bệnh án")
        );
        MedicalRecordAccess access = medicalRecordAccessRepository
                .findFirstByMedicalRecord_IdAndRoleAndDayEndIsNullOrderByDayStartDesc(
                        medicalRecord.getId(),
                        AccessRole.MAIN_DOCTOR
                ).orElseThrow(() -> new NotFoundException("Hồ sơ chưa được tiếp nhận"));

        Account doctorAccount = access.getGrantedTo();
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

    public List<EmploymentMedicalRecordResponse> getAllMedicalRecordsForEmployment() {
        return medicalRecordRepository.findAll().stream()
                .filter(medicalRecord -> medicalRecord.getUser() != null)
                .map(medicalRecord -> {
                    EmploymentMedicalRecordResponse dto = new EmploymentMedicalRecordResponse();
                    dto.setMedicalRecordId(medicalRecord.getId());

                    User user = medicalRecord.getUser();
                    Account account = user.getAccount();

                    dto.setFullName(account.getFullName());
                    dto.setGender(account.getGender());
                    dto.setDob(user.getDob());
                    dto.setPhoneNumber(account.getPhoneNumber());
                    dto.setAccountId(account.getId());

                    return dto;
                }).toList();
    }

    //    public ManagerMedicalRecordResponse getManagerMedicalRecord(Long accountId) throws NotFoundException {
//        Account account = accountService.findById(accountId);
//        User user = account.getUser();
//        if (user == null || user.getMedicalRecord() == null) {
//            throw new NotFoundException("Không tìm thấy thông tin bệnh nhân hay hồ sơ bệnh án");
//        }
//        MedicalRecord medicalRecord = user.getMedicalRecord();
//        Optional<MedicalRecordAccess> accessOpt =
//                medicalRecordAccessRepository.findByMedicalRecordAndRole(medicalRecord, AccessRole.MAIN_DOCTOR);
//        DoctorResponse doctorResponse = null;
//        if (accessOpt.isPresent()) {
//            Account doctorAccount = accessOpt.get().getGrantedTo();
//            if (doctorAccount != null && doctorAccount.getDoctor() != null) {
//                doctorResponse = new DoctorResponse(doctorAccount);
//                modelMapper.map(doctorAccount.getDoctor(), doctorResponse);
//            }
//        }
//
//        MedicalRecordResponse medicalRecordResponse = new MedicalRecordResponse();
//        modelMapper.map(medicalRecord, medicalRecordResponse);
//
//        if (user != null) {
//            Account userAcc = user.getAccount();
//            if (userAcc != null) {
//                modelMapper.map(userAcc, medicalRecordResponse);
//            }
//            modelMapper.map(user, medicalRecordResponse);
//        }
//        return new ManagerMedicalRecordResponse(medicalRecordResponse, doctorResponse);
//    }
}
