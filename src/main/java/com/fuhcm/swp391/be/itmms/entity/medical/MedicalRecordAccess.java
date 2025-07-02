package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fuhcm.swp391.be.itmms.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Date dayStart;

    @Column(name = "DayEnd", nullable = true)
    private Date dayEnd;

    @ManyToOne
    @JoinColumn(name = "GrantedBy", referencedColumnName = "Id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "GrantedTo", referencedColumnName = "Id")
    private Account grantedTo;

    @ManyToOne
    @JoinColumn(name = "MedicalRecordID", referencedColumnName = "Id")
    private MedicalRecord medicalRecord;


}
