package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fuhcm.swp391.be.itmms.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MedicalRecordAccess")
public class MedicalRecordAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "DayStart", nullable = false)
    private LocalDate dayStart;

    @Column(name = "DayEnd", nullable = true)
    private LocalDate dayEnd;

    @ManyToOne
    @JoinColumn(name = "GrantedBy", referencedColumnName = "Id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "MedicalRecordID", referencedColumnName = "Id")
    private MedicalRecord medicalRecord;
}
