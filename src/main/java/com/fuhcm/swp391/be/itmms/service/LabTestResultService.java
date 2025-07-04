package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultDTO;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.repository.LabTestResultRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabTestResultService {

    private final LabTestResultRepository labTestResultRepository;
    private final LabTestService labTestService;
    private final ModelMapper modelMapper;
    private final MedicalRecordService medicalRecordService;

    public LabTestResultService(LabTestResultRepository labTestResultRepository,
                                LabTestService labTestService, ModelMapper modelMapper, MedicalRecordService medicalRecordService) {
        this.labTestResultRepository = labTestResultRepository;
        this.labTestService = labTestService;
        this.modelMapper = modelMapper;
        this.medicalRecordService = medicalRecordService;
    }

    @Transactional
    public void sendInitLabTestRequest(LabTestResultRequest labTestResultRequest) throws NotFoundException {
        for (LabTestDTO labTestDTO : labTestResultRequest.getTests()) {
            LabTestResult labTestResult = new LabTestResult();
            labTestResult.setLabTestType(LabTestResultType.INITIAL);
            labTestResult.setStatus(LabTestResultStatus.NOT_STARTED);
            labTestResult.setTestDate(LocalDate.now());
            labTestResult.setTest(labTestService.findById(labTestDTO.getId()));
            labTestResult.setMedicalRecord(medicalRecordService.findById(labTestResultRequest.getMedicalRecordId()));
            labTestResultRepository.save(labTestResult);
        }
    }

    public List<LabTestResultResponse> getInitLabTestResults(Long medicalRecordId) {
        List<LabTestResult> results = labTestResultRepository
                .findByLabTestTypeAndMedicalRecord_Id(LabTestResultType.INITIAL, medicalRecordId);

        return results.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private LabTestResultResponse convertToResponse(LabTestResult result) {
        LabTestResultResponse response = modelMapper.map(result, LabTestResultResponse.class);
        if (result.getTest() != null) {
            response.setLabTestId(result.getTest().getId());
            response.setLabTestName(result.getTest().getName());
        }

        if (result.getAccount() != null) {
            response.setStaffAccountId(result.getAccount().getId());
            response.setStaffFullName(result.getAccount().getFullName());
        }
        return response;
    }

    public List<LabTestResultDTO> findAll() {
        List<LabTestResult> labTestResults = labTestResultRepository.findAll();

        return labTestResults.stream().map(result -> {
            LabTestResultDTO dto = new LabTestResultDTO();
            dto.setLabTestType(result.getLabTestType());
            dto.setResultSummary(result.getResultSummary());
            dto.setResultDetails(result.getResultDetails());
            dto.setStatus(result.getStatus());
            dto.setTestDate(result.getTestDate());
            dto.setNotes(result.getNotes());

            if (result.getTest() != null) {
                dto.setTestName(result.getTest().getName());
            }

            if (result.getAccount() != null) {
                String fullNameStaff = result.getAccount().getFullName();
                dto.setFullNameStaff(fullNameStaff);
                dto.setEmailStaff(result.getAccount().getEmail());
            }

            if (result.getMedicalRecord() != null && result.getMedicalRecord().getUser() != null) {
                String fullNamePatient = result.getMedicalRecord().getUser().getAccount().getFullName();
                dto.setFullNamePatient(fullNamePatient);
                dto.setPhoneNumberPatient(result.getMedicalRecord().getUser().getAccount().getPhoneNumber());
            }
            return dto;
        }).toList();
    }


//    public LabTestResult updateLabTestResult(Long id, LabTestResult updatedResult) {
//        LabTestResult existing = labTestResultRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("LabTestResult not found with id: " + id));
//
//        // update fields
//        existing.setTestDate(updatedResult.getTestDate());
//        existing.setResultSummary(updatedResult.getResultSummary());
//        existing.setResultDetails(updatedResult.getResultDetails());
//        existing.setStatus(updatedResult.getStatus());
//        existing.setNotes(updatedResult.getNotes());
//        existing.setLabTestType(updatedResult.getLabTestType());
//        existing.setTest(updatedResult.getTest());
//        existing.setAccount(updatedResult.getAccount());
//        existing.setSession(updatedResult.getSession());
//        existing.setMedicalRecord(updatedResult.getMedicalRecord());
//
//        return labTestResultRepository.save(existing);
//    }
}
