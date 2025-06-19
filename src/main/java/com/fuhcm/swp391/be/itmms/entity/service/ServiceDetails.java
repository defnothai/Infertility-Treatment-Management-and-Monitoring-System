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
    private int id;

    @Column(name = "Concept", nullable = false)
    private String concept;

    @Column(name = "Condition", nullable = false)
    private String condition;

    @Column(name = "Assignment", nullable = false)
    private String assignment;

    @Column(name = "UnAssignment", nullable = false)
    private String unAssignment;

    @Column(name = "ProcedureDetails", nullable = false)
    private String procedureDetails;

    @Column(name = "SuccessRate", nullable = false)
    private String successRate;

    @Column(name = "Experience", nullable = false)
    private String experience;

    @Column(name = "Risk", nullable = false)
    private String risk;

    @Column(name = "ProcedureName", nullable = false)
    private String procedure;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;
}
