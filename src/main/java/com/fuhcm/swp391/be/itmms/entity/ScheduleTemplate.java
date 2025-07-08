package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.constant.DayOfWeek;
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
@Table(name = "ScheduleTemplate")
public class ScheduleTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "DayOfWeek", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "RoomNumber", nullable = false)
    private int roomNumber;

    @Column(name = "MaxCapacity", nullable = false)
    private int maxCapacity;

    @ManyToOne
    @JoinColumn(name = "ShiftID", referencedColumnName = "Id")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "AssignTo", referencedColumnName = "Id")
    @JsonIgnore
    private Account account;
}
