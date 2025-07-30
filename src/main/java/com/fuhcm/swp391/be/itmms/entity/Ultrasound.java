package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.UltrasoundType;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Ultrasound")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ultrasound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Result", columnDefinition = "NVARCHAR(255)")
    private String result;

    @Column(name = "IsActive")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    private UltrasoundType type;

    @Column(name = "ImageUrls", nullable = false, columnDefinition = "TEXT")
    private String imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TreatmentSession")
    private TreatmentSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account", nullable = false)
    private Account doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MedicalRecord", nullable = false)
    private MedicalRecord medicalRecord;
}
