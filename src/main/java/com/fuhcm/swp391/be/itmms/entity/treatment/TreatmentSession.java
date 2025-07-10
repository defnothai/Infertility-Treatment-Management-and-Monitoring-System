package com.fuhcm.swp391.be.itmms.entity.treatment;

import com.fuhcm.swp391.be.itmms.entity.prescription.Prescription;
import com.fuhcm.swp391.be.itmms.entity.Ultrasound;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private LocalDate date;

    @Column(name = "Diagnosis", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String diagnosis;

    @Column(name = "Symptoms", nullable = true, columnDefinition = "NVARCHAR(255)")
    private String symptoms;

    @Column(name = "Notes", nullable = true, columnDefinition = "NVARCHAR(255)")
    private String notes;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "ProgressID", referencedColumnName = "Id")
    private TreatmentStageProgress progress;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<LabTestResult> labTestResults;

    @ManyToOne
    @JoinColumn(name = "PrescriptionID", referencedColumnName = "Id")
    private Prescription prescription;

    @OneToMany(mappedBy = "session")
    private List<Ultrasound> ultrasounds;

    @OneToMany(mappedBy = "session")
    private List<Prescription> prescriptions;

}
