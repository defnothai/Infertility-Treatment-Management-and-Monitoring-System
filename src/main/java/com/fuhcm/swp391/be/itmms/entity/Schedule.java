package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
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
    private LocalDate workDate;

    @Column(name = "RoomNumber", nullable = false, length = 5)
    private int roomNumber;

    @Column(name = "MaxCapacity", nullable = false, length = 5)
    private int maxCapacity;

    @Column(name = "CreateAt", nullable = false)
    private LocalDate createAt;

    @Column(name = "Note", nullable = true, length = 255)
    private String note;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "AssignBy", referencedColumnName = "Id")
    private Account account;

    @JsonIgnore
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "Assigned", referencedColumnName = "Id")
    private Account assignTo;

    @ManyToOne
    @JoinColumn(name = "ShiftID", referencedColumnName = "Id")
    private Shift shift;

}
