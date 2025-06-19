package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fuhcm.swp391.be.itmms.entity.Prescription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Medication")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "Form", nullable = false)
    private String form;

    @Column(name = "Strength", nullable = false)
    private String strength;

    @Column(name = "Unit", nullable = false)
    private String unit;

    @Column(name = "Manufacturer", nullable = false)
    private String manufacturer;

    @ManyToMany(mappedBy = "medications", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

}
