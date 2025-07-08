package com.fuhcm.swp391.be.itmms.entity.prescription;

import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "CreateAt", nullable = false)
    private LocalDate createAt;

    @Column(name = "Notes")
    private String notes;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<MedicationPrescription> medicationPrescriptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionId", nullable = false)
    private TreatmentSession session;


}
