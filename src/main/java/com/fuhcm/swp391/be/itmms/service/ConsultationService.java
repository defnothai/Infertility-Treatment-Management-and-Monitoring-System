package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ConsultationStatus;
import com.fuhcm.swp391.be.itmms.dto.request.ConsultationRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ConsultationResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Consultation;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ConsultationResponse createConsultation(ConsultationRequest consultationRequest) {
        Consultation consultation = new Consultation();
        consultation.setPatientName(consultationRequest.getPatientName());
        consultation.setEmail(consultationRequest.getEmail());
        consultation.setPhoneNumber(consultationRequest.getPhoneNumber());
        consultation.setMsg(consultationRequest.getMessage());
        consultation.setStatus(ConsultationStatus.PENDING);
        consultationRepository.save(consultation);
        return new ConsultationResponse(consultation.getPatientName(), consultation.getPhoneNumber(), consultation.getEmail(), consultation.getMsg());
    }

    public ConsultationResponse updateConsultation(Authentication authentication, Long id, String status) {
        Account account = accountRepository.findByEmail(authentication.getName());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        Consultation consultation =  consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        if(status.equalsIgnoreCase(ConsultationStatus.PROCESSED.toString())) {
            consultation.setStatus(ConsultationStatus.PROCESSED);
        } else if(status.equalsIgnoreCase(ConsultationStatus.DELETED.toString())) {
            consultation.setStatus(ConsultationStatus.DELETED);
        }
        consultation.setStaff(account);
        consultationRepository.save(consultation);
        return new ConsultationResponse(consultation);
    }

    public ConsultationResponse deleteConsultation(Authentication authentication, Long id) {
        Account account = accountRepository.findByEmail(authentication.getName());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        Consultation consultation =  consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        consultation.setStatus(ConsultationStatus.DELETED);
        consultation.setStaff(account);
        consultationRepository.save(consultation);
        return new ConsultationResponse(consultation);
    }

    public List<ConsultationResponse> getAllConsultations() {
        List<Consultation> consultations = consultationRepository.findAll();
        List<ConsultationResponse> response = new ArrayList<>();
        for (Consultation consultation : consultations) {
            response.add(new ConsultationResponse(consultation));
        }
        return response;
    }
}
