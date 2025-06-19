package com.fuhcm.swp391.be.itmms.entity.lab;

import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
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
@Table(name = "LabTestResult")
public class LabTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "TestDate", nullable = false)
    private Date testDate;

    @Column(name = "ResultSummary", nullable = true)
    private String resultSummary;

    @Column(name = "ResultDetails", nullable = true)
    private String resultDetails;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "LabTestID", referencedColumnName = "Id")
    private LabTest test;

    @ManyToOne
    @JoinColumn(name = "MadeBy", referencedColumnName = "Id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "SessionID", referencedColumnName = "Id")
    private TreatmentSession session;
}
