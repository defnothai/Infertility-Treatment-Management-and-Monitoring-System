package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.InvoiceStatus;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetail;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetailReminder;
import com.fuhcm.swp391.be.itmms.dto.response.InvoiceReportResponse;
import com.fuhcm.swp391.be.itmms.dto.response.InvoiceResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.User;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TreatmentPlanRepository treatmentPlanRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EmailService emailService;

    public List<InvoiceReportResponse> getInvoiceForReport(@Valid @NotNull LocalDate fromDate, @Valid @NotNull LocalDate toDate) {
        if(fromDate.isAfter(toDate)){
            throw new IllegalArgumentException("fromDate must be before toDate");
        }
        List<InvoiceReportResponse> invoiceReportResponses = new ArrayList<>();

        List<Invoice> invoices = invoiceRepository.findByDateBetween(fromDate, toDate.plusDays(1));
        LocalDate current = fromDate;
        while(!current.isAfter(toDate)){
            long deposit = 0;
            long service = 0;
            for(Invoice invoice : invoices){
                if(invoice.getDate().equals(current)){
                    if(invoice.getType().equalsIgnoreCase("DEPOSIT") && invoice.getStatus().name().equalsIgnoreCase(InvoiceStatus.PAID.name())){
                        deposit += invoice.getTotal();
                    } else if(invoice.getType().equalsIgnoreCase("SERVICE") && invoice.getStatus().name().equalsIgnoreCase(InvoiceStatus.PAID.name())){
                        service += invoice.getTotal();
                    }
                }
            }
            long total = deposit + service;
            invoiceReportResponses.add(new InvoiceReportResponse(current, deposit, service, total));
            current = current.plusDays(1);
        }

        return invoiceReportResponses;
    }

    public List<InvoiceResponse> getListInvoicesForReport(@Valid @NotNull LocalDate fromDate, @Valid @NotNull LocalDate toDate) {
        if(fromDate.isAfter(toDate)){
            throw new IllegalArgumentException("fromDate must be before toDate");
        }
        List<InvoiceResponse> responses = new ArrayList<>();
        List<Invoice> invoices = invoiceRepository.findByDateBetween(fromDate, toDate);
        for(Invoice invoice : invoices){
            if(invoice.getStatus().name().equalsIgnoreCase(InvoiceStatus.PAID.name())){
                responses.add(new InvoiceResponse(invoice));
            }
        }
        return  responses;
    }

    public InvoiceResponse createInvoice(Authentication authentication, @Valid @NotNull Long id) {
        Account account = accountRepository.findByEmail(authentication.getName());
        if(account == null){
            throw new IllegalArgumentException("Doctor not found");
        }
        MedicalRecord record = medicalRecordRepository.findById(id).orElse(null);
        if(record == null){
            throw new IllegalArgumentException("Medical record not found");
        }
        User user = record.getUser();
        Account owner = accountRepository.findByUser(user);
        TreatmentPlan plan = treatmentPlanRepository.findByMedicalRecord(record);
        List<TreatmentPlan> plans =  new ArrayList<>();
        plans.add(plan);
        com.fuhcm.swp391.be.itmms.entity.service.Service service = serviceRepository.findByPlans(plans);
        double price = service.getPrice();
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.now());
        invoice.setDiscount(0);
        invoice.setTotal(price);
        invoice.setFinalAmount(price);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setNotes(null);
        invoice.setType("SERVICE");
        invoice.setOrderId(null);
        invoice.setOwner(owner);
        invoice.setAccount(account);
        invoice.setTreatmentPlan(plan);
        invoice.setInvoicePayments(null);
        invoiceRepository.save(invoice);
        String link = paymentService.createPaymentLink(invoice.getId());
        EmailDetail detail = reminderService.buildEmailForPayment(link,owner.getEmail(), owner.getFullName(), invoice.getFinalAmount());
        emailService.sendPaymentEmail(detail);
        return new InvoiceResponse(invoice);
    }
}
