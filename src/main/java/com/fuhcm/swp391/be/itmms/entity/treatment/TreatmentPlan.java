package com.fuhcm.swp391.be.itmms.entity.treatment;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.Service;
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
@Table(name = "TreatmentPlan")
public class TreatmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "DateStart")
    private LocalDate dayStart;

    @Column(name = "DateEnd")
    private LocalDate dayEnd;

    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TreatmentPlanStatus status;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;

    @OneToOne
    @JoinColumn(name = "MedicalRecordID", referencedColumnName = "Id")
    private MedicalRecord medicalRecord;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<TreatmentStageProgress> treatmentStageProgress;

    @OneToOne(mappedBy = "treatmentPlan")
    private Invoice invoice;
}
