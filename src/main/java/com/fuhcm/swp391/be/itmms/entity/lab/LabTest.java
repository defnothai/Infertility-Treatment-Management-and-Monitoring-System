package com.fuhcm.swp391.be.itmms.entity.lab;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LabTest")
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false, columnDefinition = "NVARCHAR(250)")
    private String name;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "SampleType", nullable = false)
    private String sampleType;

    @Column(name = "EstimatedTime", nullable = false)
    private LocalTime estimatedTime;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<LabTestResult> results;
}
