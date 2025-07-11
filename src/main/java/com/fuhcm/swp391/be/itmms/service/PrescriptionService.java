package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.MedicationPrescriptionRequest;
import com.fuhcm.swp391.be.itmms.dto.request.PrescriptionRequest;
import com.fuhcm.swp391.be.itmms.dto.request.PrescriptionUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.MedicationPrescriptionResponse;
import com.fuhcm.swp391.be.itmms.dto.response.PrescriptionResponse;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.prescription.Medication;
import com.fuhcm.swp391.be.itmms.entity.prescription.MedicationPrescription;
import com.fuhcm.swp391.be.itmms.entity.prescription.Prescription;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.repository.MedicationRepository;
import com.fuhcm.swp391.be.itmms.repository.PrescriptionRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final TreatmentSessionRepository sessionRepository;
    private final MedicationRepository medicationRepository;
    private final ModelMapper modelMapper;
    private final MedicalRecordAccessService medicalRecordAccessService;

    public PrescriptionResponse createPrescription(PrescriptionRequest request) throws NotFoundException {
        TreatmentSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin buổi khám"));

        MedicalRecord medicalRecord = session.getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canCreate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }

        Prescription prescription = new Prescription();
        prescription.setSession(session);
        prescription.setCreateAt(LocalDate.now());
        prescription.setNotes(request.getNotes());

        List<MedicationPrescription> medicationPrescriptions = new ArrayList<>();
        for (MedicationPrescriptionRequest medReq : request.getMedications()) {
            Medication medication = medicationRepository.findById(medReq.getMedicationId())
                    .orElseThrow(() -> new NotFoundException("Loại thuốc không tồn tại"));

            MedicationPrescription medPres = modelMapper.map(medReq, MedicationPrescription.class);
            medPres.setMedication(medication);
            medPres.setPrescription(prescription);
            medPres.setActive(true);

            medicationPrescriptions.add(medPres);
        }
        prescription.setMedicationPrescriptions(medicationPrescriptions);
        Prescription saved = prescriptionRepository.save(prescription);
        PrescriptionResponse response = modelMapper.map(saved, PrescriptionResponse.class);
        List<MedicationPrescriptionResponse> medResponses = new ArrayList<>();
        for (MedicationPrescription mp : saved.getMedicationPrescriptions()) {
            MedicationPrescriptionResponse res = modelMapper.map(mp, MedicationPrescriptionResponse.class);
            res.setMedicationName(mp.getMedication().getName());
            medResponses.add(res);
        }
        response.setMedications(medResponses);
        return response;
    }

    public PrescriptionResponse updatePrescription(Long id, PrescriptionUpdateRequest request) throws NotFoundException {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn thuốc"));

        MedicalRecord medicalRecord = prescription.getSession().getProgress().getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        prescription.setNotes(request.getNotes());

        for (MedicationPrescription oldMed : prescription.getMedicationPrescriptions()) {
            oldMed.setActive(false);
        }

        List<MedicationPrescription> newMedications = new ArrayList<>();
        for (MedicationPrescriptionRequest medReq : request.getMedications()) {
            Medication medication = medicationRepository.findById(medReq.getMedicationId())
                    .orElseThrow(() -> new NotFoundException("Thuốc không tồn tại: " + medReq.getMedicationId()));

            MedicationPrescription medPres = modelMapper.map(medReq, MedicationPrescription.class);
            medPres.setMedication(medication);
            medPres.setPrescription(prescription);
            medPres.setActive(true);

            newMedications.add(medPres);
        }

        prescription.getMedicationPrescriptions().addAll(newMedications);
        Prescription saved = prescriptionRepository.save(prescription);
        PrescriptionResponse response = modelMapper.map(saved, PrescriptionResponse.class);
        List<MedicationPrescriptionResponse> medResponses = new ArrayList<>();
        for (MedicationPrescription mp : saved.getMedicationPrescriptions()) {
            if (mp.isActive()) {
                MedicationPrescriptionResponse medRes = modelMapper.map(mp, MedicationPrescriptionResponse.class);
                medRes.setMedicationName(mp.getMedication().getName());
                medResponses.add(medRes);
            }
        }
        response.setMedications(medResponses);
        return response;
    }


    public List<PrescriptionResponse> getAllBySessionId(Long sessionId) throws NotFoundException {
        List<Prescription> prescriptions = prescriptionRepository.findAllBySession_Id(sessionId);
        if (prescriptions.isEmpty()) {
            throw new NotFoundException("Không có đơn thuốc nào cho buổi khám này");
        }
        List<PrescriptionResponse> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            PrescriptionResponse res = modelMapper.map(p, PrescriptionResponse.class);
            List<MedicationPrescriptionResponse> meds = p.getMedicationPrescriptions().stream()
                    .filter(MedicationPrescription::isActive)
                    .map(mp -> {
                        MedicationPrescriptionResponse r = modelMapper.map(mp, MedicationPrescriptionResponse.class);
                        r.setMedicationName(mp.getMedication().getName());
                        return r;
                    })
                    .toList();
            res.setMedications(meds);
            result.add(res);
        }
        return result;
    }




}
