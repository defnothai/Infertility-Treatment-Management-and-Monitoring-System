package com.fuhcm.swp391.be.itmms.entity.prescription;

import com.fuhcm.swp391.be.itmms.constant.MedicationRoute;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MedicationPrescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicationPrescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "Dosage", nullable = false)
    private String dosage;

    @Column(name = "Frequency", nullable = false)
    private String frequency;

    @Column(name = "UsageInstruction", nullable = false)
    private String usageInstruction;

    @Enumerated(EnumType.STRING)
    @Column(name = "Route", nullable = false)
    private MedicationRoute route;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PrescriptionId", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MedicationId", nullable = false)
    private Medication medication;
}
