package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.service.LabTestResultService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LabTestResultController {

    private final LabTestResultService labTestResultService;

    public LabTestResultController(LabTestResultService labTestResultService) {
        this.labTestResultService = labTestResultService;
    }

    @PostMapping("/api/lab-test-result/init/{recordId}")
    public ResponseEntity sendInitLabTestRequest(@PathVariable("recordId") Long recordId,
            @RequestBody LabTestResultRequest labTestResultRequest) throws NotFoundException {
        List<LabTestResultResponse> responses = labTestResultService.sendInitLabTestRequest(recordId, labTestResultRequest);
        if (responses.size() <= 0) {
            throw new RuntimeException("Gửi yêu cầu xét nghiệm thất bại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                            "SEND_TEST_REQUEST_SUCCESS",
                                            "Gửi yêu cầu xét nghiệm thành công",
                                            responses));
    }

    @GetMapping("/api/lab-test-result/init/{recordId}")
    public ResponseEntity sendInitLabTestRequest(@PathVariable("recordId") Long recordId)
                                                    throws NotFoundException {
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                    "FETCH_DATA_SUCCESS",
                                                    "Lấy dữ liệu thành công",
                                                        labTestResultService.getInitLabTestResults(recordId)));
    }

    @GetMapping("/api/lab-test-result")
    public ResponseEntity<?> getLabTestResult() {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách xét nghiệm thành công",
                        labTestResultService.findAll()
                )
        );
    }





}
