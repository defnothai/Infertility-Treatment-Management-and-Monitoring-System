package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
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
@Table(name = "Schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "WorkDate", nullable = false)
    private Date workDate;

    @Column(name = "RoomNumber", nullable = false, length = 5)
    private int roomNumber;

    @Column(name = "MaxCapacity", nullable = false, length = 5)
    private int maxCapacity;

    @Column(name = "CreateAt", nullable = false)
    private Date createAt;

    @Column(name = "Note", nullable = true, length = 255)
    private String note;

    @ManyToOne
    @JoinColumn(name = "AssignBy", referencedColumnName = "Id")
    private Account account;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @ManyToOne
    @JoinColumn(name = "DoctorID", referencedColumnName = "Id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "ShiftID", referencedColumnName = "Id")
    private Shift shift;
}
