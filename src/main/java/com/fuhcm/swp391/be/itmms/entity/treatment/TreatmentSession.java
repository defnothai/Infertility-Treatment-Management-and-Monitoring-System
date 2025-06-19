package com.fuhcm.swp391.be.itmms.entity.treatment;

import com.fuhcm.swp391.be.itmms.entity.Prescription;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
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
@Table(name = "TreatmentSession")
public class TreatmentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Date", nullable = false)
    private Date date;

    @Column(name = "Diagnosis", nullable = false)
    private String diagnosis;

    @Column(name = "Symptoms", nullable = true)
    private String symptoms;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "TreatmentPlanID", referencedColumnName = "Id")
    private TreatmentPlan plan;

    @ManyToOne
    @JoinColumn(name = "ProgressID", referencedColumnName = "Id")
    private TreatmentStageProgress progress;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<LabTestResult> labTestResults;

    @ManyToOne
    @JoinColumn(name = "PrescriptionID", referencedColumnName = "Id")
    private Prescription prescription;
}
