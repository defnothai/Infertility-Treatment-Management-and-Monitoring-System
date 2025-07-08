package com.fuhcm.swp391.be.itmms.entity.service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ServiceDetails")
public class ServiceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Lob
    @Column(name = "Concept", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String concept;

    @Column(name = "ConceptImgUrl", nullable = false)
    @Lob
    private String conceptImgUrl;

    @Lob
    @Column(name = "Condition", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String condition;

    @Lob
    @Column(name = "Assignment", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String assignment;

    @Lob
    @Column(name = "UnAssignment", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String unAssignment;

    @Lob
    @Column(name = "ProcedureDetails", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String procedureDetails;

    @Column(name = "ProcedureDetailsImgUrl", nullable = false)
    @Lob
    private String procedureDetailsImgUrl;

    @Lob
    @Column(name = "SuccessRate", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String successRate;

    @Lob
    @Column(name = "Experience", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String experience;

    @Lob
    @Column(name = "Risk", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String risk;

    @Lob
    @Column(name = "HospitalProcedure", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String hospitalProcedure;

    @Lob
    @Column(name = "HospitalProcedureImgUrl", nullable = false)
    private String hospitalProcedureImgUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;

}
