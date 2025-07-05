package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultDTO;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.LabTestResultRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LabTestResultService {

    private final LabTestResultRepository labTestResultRepository;
    private final LabTestService labTestService;
    private final ModelMapper modelMapper;
    private final MedicalRecordService medicalRecordService;
    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoleService roleService;

    public LabTestResultService(LabTestResultRepository labTestResultRepository,
                                LabTestService labTestService,
                                ModelMapper modelMapper,
                                MedicalRecordService medicalRecordService,
                                AccountRepository accountRepository,
                                ScheduleRepository scheduleRepository, RoleService roleService) {
        this.labTestResultRepository = labTestResultRepository;
        this.labTestService = labTestService;
        this.modelMapper = modelMapper;
        this.medicalRecordService = medicalRecordService;
        this.accountRepository = accountRepository;
        this.scheduleRepository = scheduleRepository;
        this.roleService = roleService;
    }

    @Transactional
    public List<LabTestResultResponse> sendInitLabTestRequest(Long recordId,
                                                              LabTestResultRequest labTestResultRequest) throws NotFoundException {
        List<LabTestResultResponse> responses = new ArrayList<>();

        for (Long testId : labTestResultRequest.getTestIds()) {
            LabTestResult labTestResult = new LabTestResult();
            labTestResult.setLabTestType(LabTestResultType.INITIAL);
            labTestResult.setStatus(LabTestResultStatus.PROCESSING);
            labTestResult.setTestDate(LocalDate.now());
            labTestResult.setTest(labTestService.findById(testId));
            labTestResult.setMedicalRecord(medicalRecordService.findById(recordId));
            labTestResult.setAccount(findLeastBusyStaff(LocalDate.now()));

            LabTestResult saved = labTestResultRepository.save(labTestResult);
            LabTestResultResponse response = modelMapper.map(saved, LabTestResultResponse.class);
            response.setLabTestName(saved.getTest().getName());
            response.setLabTestId(saved.getTest().getId());
            response.setStaffFullName(saved.getAccount().getFullName());
            response.setStaffAccountId(saved.getAccount().getId());

            responses.add(response);
        }

        return responses;
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

    private Account findLeastBusyStaff(LocalDate date) throws NotFoundException {
        LocalTime currentTime = LocalTime.now();

        List<Schedule> activeSchedules = scheduleRepository.findByWorkDateAndStatus(date, ScheduleStatus.WORKING);
        if (activeSchedules.isEmpty()) {
            throw new NotFoundException("Không có nhân viên làm việc trong hôm nay");
        }

        List<Account> eligibleStaff = new ArrayList<>();
        for (Schedule schedule : activeSchedules) {
            Shift shift = schedule.getShift();
            if (shift != null &&
                    currentTime.isAfter(shift.getStartTime()) &&
                    currentTime.isBefore(shift.getEndTime()) &&
                    schedule.getStatus().equals(ScheduleStatus.WORKING) &&
                    schedule.getAssignTo().getRoles().contains(roleService.findByRoleName(AccountRole.ROLE_STAFF))) {
                eligibleStaff.add(schedule.getAssignTo());
            }
        }

        if (eligibleStaff.isEmpty()) {
            throw new NotFoundException("Không có nhân viên làm việc trong ca làm hiện tại");
        }

        List<Long> staffIds = labTestResultRepository.findLeastBusyAccountIdByDateAndShift(date, LocalTime.now().toString());

        if (staffIds.isEmpty()) {
            return eligibleStaff.getFirst();
        }else {
            return accountRepository.findById(staffIds.getFirst()).orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên"));
        }
    }


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

