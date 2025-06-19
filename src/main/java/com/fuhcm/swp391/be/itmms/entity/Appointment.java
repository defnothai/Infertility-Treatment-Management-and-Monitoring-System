package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
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
@Table(name = "Appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Time", nullable = false)
    private Date time;

    @Column(name = "NumericalOrder", nullable = false)
    private int numericalOrder;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;

    @Column(name = "Note", nullable = true, length = 255)
    private String note;

    @Column(name = "CreateAt", nullable = false)
    private Date createAt;

    @Column(name = "PatientName", nullable = false, length = 20)
    private String patientName;

    @ManyToOne
    @JoinColumn(name = "Schedule_Id", referencedColumnName = "Id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "BookBy", referencedColumnName = "Id")
    private User user;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private TreatmentPlan treatmentPlan;
}
