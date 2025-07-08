package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultForStaffRequest;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultForStaffResponse;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestResultService {

    private final LabTestResultRepository labTestResultRepository;
    private final LabTestService labTestService;
    private final ModelMapper modelMapper;
    private final MedicalRecordService medicalRecordService;
    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoleService roleService;
    private final TreatmentSessionRepository treatmentSessionRepository;
    private final LabTestRepository labTestRepository;

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
            response.setStaffFullName(saved.getAccount().getFullName());
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
            response.setLabTestName(result.getTest().getName());
        }

        if (result.getAccount() != null) {
            response.setStaffFullName(result.getAccount().getFullName());
        }
        return response;
    }


    public List<LabTestResultResponse> findAll() {
        return labTestResultRepository.findAll().stream().map(entity -> {
            LabTestResultResponse dto = modelMapper.map(entity, LabTestResultResponse.class);

            if (entity.getTest() != null) {
                dto.setLabTestName(entity.getTest().getName());
            }

            if (entity.getAccount() != null) {
                dto.setStaffFullName(entity.getAccount().getFullName());
            }
            return dto;
        }).collect(Collectors.toList());
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

    @Transactional
    public LabTestResultForStaffResponse updateLabTestResultForStaff(Long id, LabTestResultForStaffRequest request) throws NotFoundException {
        LabTestResult labTestResult = labTestResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kết quả xét nghiệm không tồn tại"));

        labTestResult.setResultSummary(request.getResultSummary());
        labTestResult.setResultDetails(request.getResultDetails());
        labTestResult.setStatus(request.getStatus());
        labTestResult.setNotes(request.getNotes());

        labTestResultRepository.save(labTestResult);

        LabTestResultForStaffResponse response = modelMapper.map(labTestResult, LabTestResultForStaffResponse.class);

        if (labTestResult.getTest() != null) {
            response.setLabTestName(labTestResult.getTest().getName());
        }

        if (labTestResult.getMedicalRecord() != null && labTestResult.getMedicalRecord().getUser() != null) {
            User patient = labTestResult.getMedicalRecord().getUser();
            response.setPatientFullName(patient.getAccount().getFullName());
            response.setPatientDob(patient.getDob().toString());
            response.setPatientPhoneNumber(patient.getAccount().getPhoneNumber());
            response.setPatientGender(patient.getAccount().getGender());
        }
        return response;
    }


    public List<LabTestResultForStaffResponse> searchLabTestResults(String phoneNumber, String fullName, LocalDate testDate) {
        List<LabTestResult> results = labTestResultRepository.searchByFilters(phoneNumber, fullName, testDate);

        return results.stream().map(ltr -> {
            LabTestResultForStaffResponse response = modelMapper.map(ltr, LabTestResultForStaffResponse.class);

            if (ltr.getTest() != null) {
                response.setLabTestName(ltr.getTest().getName());
            }

            if (ltr.getMedicalRecord() != null && ltr.getMedicalRecord().getUser() != null) {
                Account acc = ltr.getMedicalRecord().getUser().getAccount();
                response.setPatientFullName(acc.getFullName());
                response.setPatientPhoneNumber(acc.getPhoneNumber());
                response.setPatientGender(acc.getGender());
                if (acc.getUser() != null) {
                    response.setPatientDob(acc.getUser().getDob().toString());
                }
            }
            return response;
        }).toList();
    }

    @Transactional
    public List<LabTestResultResponse> sendFollowUpLabTestResultsRequest(Long recordId, Long sessionId, LabTestResultRequest request) throws NotFoundException, BadRequestException {
        TreatmentSession session = treatmentSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin buổi khám"));

        MedicalRecord medicalRecord = medicalRecordService.findById(recordId);

        Account staff = this.findLeastBusyStaff(LocalDate.now());

        List<LabTest> tests = labTestRepository.findAllById(request.getTestIds());
        if (tests.isEmpty()) {
            throw new BadRequestException("Không có sẵn dịch vụ cho các loại xét nghiệm này");
        }

        List<LabTestResult> savedResults = new ArrayList<>();
        for (LabTest test : tests) {
            LabTestResult result = new LabTestResult();
            result.setTestDate(LocalDate.now());
            result.setStatus(LabTestResultStatus.PROCESSING);
            result.setLabTestType(LabTestResultType.FOLLOW_UP);
            result.setTest(test);
            result.setAccount(staff);
            result.setSession(session);
            result.setMedicalRecord(medicalRecord);
            savedResults.add(labTestResultRepository.save(result));
        }

        return savedResults.stream().map(result -> {
            LabTestResultResponse dto = new LabTestResultResponse();
            dto.setId(result.getId());
            dto.setTestDate(result.getTestDate());
            dto.setStatus(result.getStatus());
            dto.setLabTestName(result.getTest().getName());
            dto.setStaffFullName(result.getAccount().getFullName());
            return dto;
        }).toList();
    }



}


