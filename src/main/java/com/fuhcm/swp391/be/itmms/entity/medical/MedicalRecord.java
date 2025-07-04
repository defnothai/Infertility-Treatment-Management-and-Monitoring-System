package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.User;
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
@Table(name = "MedicalRecord")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Symptoms", nullable = true, columnDefinition = "NVARCHAR(255)")
    private String symptoms;

    @Column(name = "Diagnosis", nullable = true, columnDefinition = "NVARCHAR(255)")
    private String diagnosis;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "medicalRecord")
    @JsonIgnore
    private List<MedicalRecordAccess> medicalRecordAccess;

    @OneToOne(mappedBy = "medicalRecord")
    @JsonIgnore
    private TreatmentPlan treatmentPlan;

    @OneToOne
    @JoinColumn(name = "PatientID", referencedColumnName = "Id")
    private User user;

    @OneToMany(mappedBy = "medicalRecord")
    private List<LabTestResult> labTestResults;
}
