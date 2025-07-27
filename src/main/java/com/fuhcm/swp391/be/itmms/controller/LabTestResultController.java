package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.constant.LabTestResultStatus;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultForStaffRequest;
import com.fuhcm.swp391.be.itmms.dto.request.LabTestResultRequest;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestGroupResponse;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultForStaffResponse;
import com.fuhcm.swp391.be.itmms.dto.response.LabTestResultResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.LabTestResultService;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LabTestResultController {

    private final LabTestResultService labTestResultService;

    public LabTestResultController(LabTestResultService labTestResultService) {
        this.labTestResultService = labTestResultService;
    }

    // ========================================== PAGE 1 =================================================================

    // doctor gửi yêu cầu xét nghiệm ban đầu
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


    // =========================================== PAGE 2 =============================================================
    // doctor yêu cầu xét nghiệm khi tái khám
    @PostMapping("/api/medical-record/{recordId}/treatment-sessions/{sessionId}/lab-test-results")
    public ResponseEntity<?> sendFollowUpLabTestResultsRequest(
            @PathVariable("recordId") Long recordId,
            @PathVariable("sessionId") Long sessionId,
            @RequestBody LabTestResultRequest request) throws NotFoundException, BadRequestException {
        List<LabTestResultResponse> response = labTestResultService.sendFollowUpLabTestResultsRequest(recordId, sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormat<>(
                        HttpStatus.CREATED.value(),
                        "CREATE_SUCCESS",
                        "Tạo danh sách xét nghiệm thành công",
                        response
                ));
    }

    // lấy ra danh sách lab test theo id của session
    @GetMapping("/api/session/{sessionId}/lab-test-results")
    public ResponseEntity getBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách xét nghiệm thành công",
                        labTestResultService.getLabTestResultsBySessionId(sessionId)
                )
        );
    }

    // =========================================== STAFF ==============================================================

    // hiện danh sách xét nghiệm cho staff
    // cũ rồi
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

    // staff filter kết quả xét nghiệm
    // cũ rồi
    @GetMapping("/api/lab-test-result/search")
    public ResponseEntity<?> searchLabTestResults(
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate testDate) throws NotFoundException {

        List<LabTestResultForStaffResponse> response =
                labTestResultService.searchLabTestResults(phoneNumber, fullName, testDate);
        if (response.isEmpty()) {
            throw new NotFoundException("Không tồn tại kết quả xét nghiệm");
        }
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Tìm kiếm kết quả xét nghiệm thành công",
                        response
                )
        );
    }

    // lấy danh sách các nhóm yêu cầu xét nghiệm (cái này fe chưa có)
    // có thể search được
    @GetMapping("/api/lab-test-result/grouped")
    public ResponseEntity<?> getGroupedLabTests(
            @RequestParam(required = false) String keyword, // tên hoặc số điện thoại
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate testDate,
            @RequestParam(required = false) LabTestResultStatus status
    ) throws NotFoundException {
        List<LabTestGroupResponse> response = labTestResultService.findGroupedByPatientAndDate(keyword, testDate, status);
        if (response.isEmpty()) {
            throw new NotFoundException("Chưa có yêu cầu xét nghiệm");
        }
        return ResponseEntity.ok(
                new ResponseFormat<>(200, "FETCH_SUCCESS", "Lấy nhóm xét nghiệm thành công", response)
        );
    }

    // lấy chi tiết danh sách yêu cầu xn từ nhóm (cái này fe chưa có)
    @PostMapping("/api/lab-test-result/grouped/details")
    public ResponseEntity<?> getDetailsByGroup(@RequestBody List<Long> labIds) {
        List<LabTestResultForStaffResponse> response = labTestResultService.getDetailsByGroup(labIds);
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy chi tiết xét nghiệm thành công",
                        response)
        );
    }

    // staff update xét nghiệm
    @PutMapping("/api/lab-test-result/{id}/staff")
    public ResponseEntity<?> updateLabTestResultForStaff(
            @PathVariable Long id,
            @RequestBody LabTestResultForStaffRequest request) throws NotFoundException {
        LabTestResultForStaffResponse response = labTestResultService.updateLabTestResultForStaff(id, request);
        return ResponseEntity.ok(
                new ResponseFormat<>(
                        HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật kết quả xét nghiệm thành công",
                        response
                )
        );
    }


    // staff view các danh sách yêu cầu xn ban đầu của các bệnh nhân chưa thanh toán
//    @GetMapping("/api/lab-test-result/unpaid")
//    public ResponseEntity getUnpaidTests() throws NotFoundException {
//        List<LabTestResultForStaffResponse> response =
//                labTestResultService.getUnpaidGroupedTestRequests();
//        if (response.isEmpty()) {
//            throw new NotFoundException("Chưa có yêu cầu thanh toán nào");
//        }
//        return ResponseEntity.ok(
//                new ResponseFormat<>(
//                        HttpStatus.OK.value(),
//                        "FETCH_DATA_SUCCESS",
//                        "Lấy danh sách yêu cầu thanh toán thành công",
//                        response
//                )
//        );
//    }



    // ===================================================================================================================

//    @GetMapping("/api/lab-test-result/{id}/staff")
//    public ResponseEntity getLabTestResultById(@PathVariable Long id) {
//        return ResponseEntity.ok(
//                new ResponseFormat<>(
//                        HttpStatus.OK.value(),
//                        "FETCH_DATA_SUCCESS",
//                        "Tìm kiếm kết quả xét nghiệm thành công",
//                        labTestResultService.getLabTestResultInfoById(id)
//                )
//        );
//    }

//    // lấy kết quả xét nghiệm ban đầu ở trang đầu hồ sơ bệnh nhân
//    @GetMapping("/api/lab-test-result/init/{recordId}")
//    public ResponseEntity getInitLabTestRequest(@PathVariable("recordId") Long recordId)
//            throws NotFoundException {
//        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
//                "FETCH_DATA_SUCCESS",
//                "Lấy dữ liệu thành công",
//                labTestResultService.getInitLabTestResults(recordId)));
//    }

}
