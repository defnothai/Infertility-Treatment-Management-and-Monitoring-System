package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.UltrasoundType;
import com.fuhcm.swp391.be.itmms.dto.request.UltrasoundRequest;
import com.fuhcm.swp391.be.itmms.dto.response.UltrasoundResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Ultrasound;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import com.fuhcm.swp391.be.itmms.repository.UltrasoundRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UltrasoundService {

    private final UltrasoundRepository ultrasoundRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public List<UltrasoundResponse> getInitialUltrasoundsByMedicalRecordId(Long medicalRecordId) {
        List<Ultrasound> ultrasounds = ultrasoundRepository.findByMedicalRecordIdAndTypeAndIsActiveTrue(
                medicalRecordId, UltrasoundType.INITIAL
        );

        return ultrasounds.stream()
                .map(ultrasound -> {
                    UltrasoundResponse response = modelMapper.map(ultrasound, UltrasoundResponse.class);
                    response.setImgUrls(List.of(ultrasound.getImageUrls().split(";")));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public UltrasoundResponse createInitUltrasound(UltrasoundRequest request, Authentication authentication) {
        Ultrasound ultrasound = new Ultrasound();
        ultrasound.setDate(LocalDate.now());
        ultrasound.setResult(request.getResult());
        ultrasound.setType(UltrasoundType.INITIAL);
        ultrasound.setActive(true);
        ultrasound.setImageUrls(String.join(";", request.getImageUrls()));

        Account doctor = accountRepository.findByEmail(authentication.getName());
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ bệnh án không tồn tại"));

        ultrasound.setDoctor(doctor);
        ultrasound.setMedicalRecord(medicalRecord);
        Ultrasound saved = ultrasoundRepository.save(ultrasound);

        UltrasoundResponse response = modelMapper.map(saved, UltrasoundResponse.class);
        response.setImgUrls(List.of(saved.getImageUrls().split(";")));
        return response;
    }

    public UltrasoundResponse updateUltrasound(Long id, UltrasoundRequest request) {
        Ultrasound ultrasound = ultrasoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bản ghi siêu âm không tồn tại"));

        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ bệnh án không tồn tại"));

        ultrasound.setResult(request.getResult());
        ultrasound.setImageUrls(String.join(";", request.getImageUrls()));
        ultrasound.setMedicalRecord(medicalRecord);

        Ultrasound updated = ultrasoundRepository.save(ultrasound);

        UltrasoundResponse response = modelMapper.map(updated, UltrasoundResponse.class);
        response.setImgUrls(List.of(updated.getImageUrls().split(";")));
        return response;
    }

    public void deleteUltrasound(Long id) {
        Ultrasound ultrasound = ultrasoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bản ghi siêu âm không tồn tại"));

        ultrasound.setActive(false);
        ultrasoundRepository.save(ultrasound);
    }


}

