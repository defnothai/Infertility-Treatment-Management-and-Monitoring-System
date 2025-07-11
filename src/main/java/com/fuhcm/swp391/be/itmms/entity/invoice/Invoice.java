package com.fuhcm.swp391.be.itmms.entity.invoice;

import com.fuhcm.swp391.be.itmms.constant.InvoiceStatus;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Total", nullable = false)
    private double total;

    @Column(name = "Discount", nullable = true)
    private double discount;

    @Column(name = "FinalAmount", nullable = false)
    private double finalAmount;

    @Column(name = "Status", nullable = false)
    private InvoiceStatus status;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @Column(name = "Type", nullable = false)
    private String type;

    @Column(name = "OrderId", nullable = true)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "Owner", referencedColumnName = "Id")
    private Account owner;

    @ManyToOne
    @JoinColumn(name = "CreateBy", referencedColumnName = "Id")
    private Account account;

    @OneToOne
    @JoinColumn(name = "TreatmentPlanID", referencedColumnName = "Id")
    private TreatmentPlan treatmentPlan;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoicePayment> invoicePayments;

}
