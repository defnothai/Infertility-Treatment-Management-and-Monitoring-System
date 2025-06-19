package com.fuhcm.swp391.be.itmms.entity.treatment;

import com.fuhcm.swp391.be.itmms.entity.Appointment;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Date dayStart;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;

    @OneToOne
    @JoinColumn(name = "AppointmentID", referencedColumnName = "Id")
    private Appointment appointment;

    @OneToOne
    @JoinColumn(name = "MedicalRecordID", referencedColumnName = "Id")
    private MedicalRecord medicalRecord;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<TreatmentStageProgress> treatmentStageProgress;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<TreatmentSession> treatmentSessions;

    @OneToOne(mappedBy = "treatmentPlan", cascade = CascadeType.ALL)
    private Invoice invoice;
}
