package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;    
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalTime;

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
    private LocalDate time;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "Status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "Note", nullable = true, length = 255)
    private String note;

    @Column(name = "CreateAt", nullable = false)
    private LocalDate createAt;

    @Column(name = "PatientName", nullable = false, length = 20)
    private String patientName;

    @ManyToOne
    @JoinColumn(name = "Schedule_Id", referencedColumnName = "Id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "BookBy", referencedColumnName = "Id")
    private Account user;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private TreatmentPlan treatmentPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Doctor", nullable = false)
    private Account doctor;
}
