package com.fuhcm.swp391.be.itmms.entity.medical;

import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MedicalRecord")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @Column(name = "FollowUpdate", nullable = false)
    private Date followUpdate;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private List<MedicalRecordAccess> medicalRecordAccess;

    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private TreatmentPlan treatmentPlan;

    @OneToOne
    @JoinColumn(name = "PatientID", referencedColumnName = "Id")
    private User user;
}
