package com.fuhcm.swp391.be.itmms.entity.invoice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "InvoicePayment")
public class InvoicePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "PaidAmount", nullable = false)
    private double paidAmount;

    @Column(name = "PaymentMethod", nullable = false)
    private String paymentMethod;

    @Column(name = "Date", nullable = false)
    private Date date;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "InvoiceID", referencedColumnName = "Id")
    private Invoice invoice;
}
