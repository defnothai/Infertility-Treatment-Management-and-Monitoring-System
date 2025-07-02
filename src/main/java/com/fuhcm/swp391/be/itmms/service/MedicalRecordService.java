package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.PatientInfoDetails;
import com.fuhcm.swp391.be.itmms.dto.request.MedicalRecordRequest;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordAccessRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserService userService;
    private final MedicalRecordAccessRepository medicalRecordAccessRepository;
    private final AuthenticationService authenticationService;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                UserService userService,
                                MedicalRecordAccessRepository medicalRecordAccessRepository,
                                AuthenticationService authenticationService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userService = userService;
        this.medicalRecordAccessRepository = medicalRecordAccessRepository;
        this.authenticationService = authenticationService;
    }


    public MedicalRecord findById(Long id) throws NotFoundException {
        return medicalRecordRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Hồ sơ không tồn tại"));
    }

    @Transactional
    public MedicalRecord getMedicalRecord(Long userId) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordRepository.findByUser(this.userService.findById(userId));
        if (medicalRecord == null) {
            medicalRecord = new MedicalRecord();
            medicalRecord.setUser(userService.findById(userId));
            medicalRecord.setNotes(null);
            medicalRecord.setFollowUpdate(LocalDate.now());

            Account currentAccount = authenticationService.getCurrentAccount();
            MedicalRecordAccess medicalRecordAccess = new MedicalRecordAccess();
            medicalRecordAccess.setAccount(currentAccount);
            medicalRecordAccess.setMedicalRecord(medicalRecord);
            medicalRecordAccess.setDayStart(LocalDate.now());
            medicalRecordAccess.setDayEnd(null);
            medicalRecord.setMedicalRecordAccess(List.of(medicalRecordAccessRepository.save(medicalRecordAccess)));
            medicalRecord = medicalRecordRepository.save(medicalRecord);
        }
        return medicalRecord;
    }

    public MedicalRecord updateMedicalRecord(Long recordId,
                                             MedicalRecordRequest medicalRecordRequest) throws NotFoundException {
        MedicalRecord medicalRecord = this.findById(recordId);
        medicalRecord.setNotes(medicalRecordRequest.getNotes());
        return medicalRecordRepository.save(medicalRecord);
    }
}
