package com.fuhcm.swp391.be.itmms.entity.prescription;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Medication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String name;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "Form", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String form;

    @Column(name = "Strength", nullable = false)
    private String strength;

    @Column(name = "Unit", nullable = false)
    private String unit;

    @Column(name = "Manufacturer", nullable = false, columnDefinition = "NVARCHAR(250)")
    private String manufacturer;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL)
    private List<MedicationPrescription> medicationPrescriptions;
}
