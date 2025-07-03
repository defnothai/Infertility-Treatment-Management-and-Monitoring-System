package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.constant.LabTestResultType;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.repository.LabTestResultRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LabTestResultService {

    private final LabTestResultRepository labTestResultRepository;
    private final LabTestService labTestService;

    public LabTestResultService(LabTestResultRepository labTestResultRepository,
                                 LabTestService labTestService) {
        this.labTestResultRepository = labTestResultRepository;
        this.labTestService = labTestService;
    }

    @Transactional
    public void sendInitLabTestRequest(LabTestResultRequest labTestResultRequest) throws NotFoundException {
        for (LabTestDTO labTestDTO : labTestResultRequest.getTests()) {
            LabTestResult labTestResult = new LabTestResult();
            labTestResult.setLabTestType(LabTestResultType.INITIAL);
            labTestResult.setStatus(LabTestResultStatus.NOT_STARTED);
            labTestResult.setTestDate(LocalDate.now());
            labTestResult.setTest(labTestService.findById(labTestDTO.getId()));
            labTestResultRepository.save(labTestResult);
        }
    }


    public List<LabTestResult> findAll() {
        return labTestResultRepository.findAll();
    }
}
