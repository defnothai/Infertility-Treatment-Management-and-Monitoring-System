package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import com.fuhcm.swp391.be.itmms.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime dayStart;

    @Column(name = "DayEnd", nullable = true)
    private LocalDateTime dayEnd;

    @Column(name = "AccessRole", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessRole role;

    @Column(name = "PermissionLevel", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionLevel level;

    @ManyToOne
    @JoinColumn(name = "GrantedBy", referencedColumnName = "Id")
    private Account grantedBy;

    @ManyToOne
    @JoinColumn(name = "GrantedTo", referencedColumnName = "Id")
    private Account grantedTo;

    @ManyToOne
    @JoinColumn(name = "RevokedBy")
    private Account revokedBy;

    @Column(name = "RevokedAt")
    private LocalDateTime revokedAt;

    @ManyToOne
    @JoinColumn(name = "UpdatedBy")
    private Account updatedBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "MedicalRecordID", referencedColumnName = "Id")
    private MedicalRecord medicalRecord;


}
