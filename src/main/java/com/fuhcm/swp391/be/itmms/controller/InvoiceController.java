package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.InvoiceReportResponse;
import com.fuhcm.swp391.be.itmms.dto.response.InvoiceResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.InvoiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices/")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;


    @GetMapping("/report")
    public ResponseEntity<?> getInvoiceForReport(
            @Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
            @Valid @RequestParam("toDate") @NotNull LocalDate toDate
            ){
        List<InvoiceReportResponse> response = invoiceService.getInvoiceForReport(fromDate, toDate);
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy danh sách hóa đơn thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách hóa đơn thành công",
                        response));
    }

    @GetMapping("/list-invoices")
    public ResponseEntity<?> getListInvoicesForReport(
            @Valid @RequestParam("fromDate") @NotNull LocalDate fromDate,
            @Valid @RequestParam("toDate") @NotNull LocalDate toDate
    ){
        List<InvoiceResponse> responses = invoiceService.getListInvoicesForReport(fromDate, toDate);
        if(responses.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseFormat<>(HttpStatus.NO_CONTENT.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy danh sách hóa đơn thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_DATA_SUCCESS",
                        "Lấy danh sách hóa đơn thành công",
                        responses));
    }

    @PreAuthorize("hasAnyRole('STAFF', 'DOCTOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createInvoice(Authentication authentication,
                                           @Valid @RequestParam("id") @NotNull Long id){
        InvoiceResponse response = invoiceService.createInvoice(authentication, id);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "CREATE_DATA_FAIL",
                            "Tạo hóa đơn thất bại",
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseFormat<>(HttpStatus.OK.value(),
                        "CREATE_DATA_SUCCESS",
                        "Tạo hóa đơn thành công",
                        response));
    }
}
