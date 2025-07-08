package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.MedicationResponse;
import com.fuhcm.swp391.be.itmms.entity.prescription.Medication;
import com.fuhcm.swp391.be.itmms.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final ModelMapper modelMapper;

    public List<MedicationResponse> getAllMedications() {
        List<Medication> medications = medicationRepository.findAll();
        return medications.stream().map(med -> {
            MedicationResponse res = modelMapper.map(med, MedicationResponse.class);
            return res;
        }).toList();
    }
}
