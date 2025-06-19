package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.entity.medical.Medication;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
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
@Table(name = "Prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "CreateAt", nullable = false)
    private Date createAt;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<TreatmentSession> sessions;

    @ManyToMany
    @JoinTable(
            name = "MedicationPrescription",
            joinColumns = @JoinColumn(name = "prescriptionID"),
            inverseJoinColumns = @JoinColumn(name = "medicationID")
    )
    private List<Medication> medications;
}
