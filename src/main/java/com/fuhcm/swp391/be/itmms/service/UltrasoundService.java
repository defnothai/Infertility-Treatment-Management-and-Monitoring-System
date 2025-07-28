package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.UltrasoundType;
import com.fuhcm.swp391.be.itmms.dto.request.UltrasoundRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ImageUrlListResponse;
import com.fuhcm.swp391.be.itmms.dto.response.UltrasoundResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Ultrasound;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import com.fuhcm.swp391.be.itmms.repository.UltrasoundRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UltrasoundService {

    private final UltrasoundRepository ultrasoundRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final TreatmentSessionRepository treatmentSessionRepository;
    private final MedicalRecordAccessService medicalRecordAccessService;

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
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ bệnh án không tồn tại"));
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        Ultrasound ultrasound = new Ultrasound();
        ultrasound.setDate(LocalDate.now());
        ultrasound.setResult(request.getResult());
        ultrasound.setType(UltrasoundType.INITIAL);
        ultrasound.setActive(true);
        ultrasound.setImageUrls(String.join(";", request.getImageUrls()));

        Account doctor = accountRepository.findByEmail(authentication.getName());

        ultrasound.setDoctor(doctor);
        ultrasound.setMedicalRecord(medicalRecord);
        Ultrasound saved = ultrasoundRepository.save(ultrasound);

        UltrasoundResponse response = modelMapper.map(saved, UltrasoundResponse.class);
        response.setImgUrls(List.of(saved.getImageUrls().split(";")));
        return response;
    }

    public UltrasoundResponse updateUltrasound(Long id, UltrasoundRequest request) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ bệnh án không tồn tại"));

        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        Ultrasound ultrasound = ultrasoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bản ghi siêu âm không tồn tại"));

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
        MedicalRecord medicalRecord = ultrasound.getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canDelete(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        ultrasound.setActive(false);
        ultrasoundRepository.save(ultrasound);
    }

    @Transactional
    public UltrasoundResponse createFollowUpUltrasound(Long sessionId, UltrasoundRequest request, Authentication authentication) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new NotFoundException("Medical record not found"));

        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }

        TreatmentSession session = treatmentSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin buổi khám"));

        Account doctor = accountRepository.findByEmail(authentication.getName());

        Ultrasound ultrasound = new Ultrasound();
        ultrasound.setDate(LocalDate.now());
        ultrasound.setResult(request.getResult());
        ultrasound.setImageUrls(String.join(";", request.getImageUrls()));
        ultrasound.setType(UltrasoundType.FOLLOW_UP);
        ultrasound.setActive(true);
        ultrasound.setSession(session);
        ultrasound.setMedicalRecord(medicalRecord);
        ultrasound.setDoctor(doctor);
        ultrasound = ultrasoundRepository.save(ultrasound);

        UltrasoundResponse response = new UltrasoundResponse();
        response.setId(ultrasound.getId());
        response.setDate(ultrasound.getDate());
        response.setResult(ultrasound.getResult());
        response.setImgUrls(request.getImageUrls());

        return response;
    }

    public ImageUrlListResponse getImageUrlsById(Long id) throws NotFoundException {
        Ultrasound ultrasound = ultrasoundRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bản ghi siêu âm"));

        List<String> imageUrlList = Arrays.stream(ultrasound.getImageUrls().split(";"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .toList();
        return new ImageUrlListResponse(imageUrlList);
    }


    public List<UltrasoundResponse> getBySessionId(Long sessionId) {
        List<Ultrasound> ultrasounds = ultrasoundRepository.findBySession_IdAndIsActiveTrue(sessionId);
        return ultrasounds.stream().map(u -> {
            UltrasoundResponse dto = new UltrasoundResponse();
            dto.setId(u.getId());
            dto.setDate(u.getDate());
            dto.setResult(u.getResult());
            dto.setImgUrls(splitImageUrls(u.getImageUrls()));
            return dto;
        }).collect(Collectors.toList());
    }

    private List<String> splitImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.trim().isEmpty()) return List.of();
        return Arrays.stream(imageUrls.split(";"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

}

