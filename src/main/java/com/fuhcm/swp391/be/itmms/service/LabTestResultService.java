package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.repository.LabTestResultRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LabTestResultService {

    private final LabTestResultRepository labTestResultRepository;
    private final LabTestService labTestService;

    private LabTestResultService(LabTestResultRepository labTestResultRepository,
                                 LabTestService labTestService) {
        this.labTestResultRepository = labTestResultRepository;
        this.labTestService = labTestService;
    }

    public void sendInitLabTestRequest(LabTestResultRequest labTestResultRequest) throws NotFoundException {
        LabTestResult labTestResult = new LabTestResult();
        labTestResult.setLabTestType(LabTestResultType.INITIAL);
        labTestResult.setStatus(LabTestResultStatus.NOT_STARTED);
        labTestResult.setTestDate(LocalDate.now());
        for (LabTestDTO labTestDTO : labTestResultRequest.getTests()) {
            labTestResult.setTest(labTestService.findById(labTestDTO.getId()));
            labTestResultRepository.save(labTestResult);
        }
    }
}
