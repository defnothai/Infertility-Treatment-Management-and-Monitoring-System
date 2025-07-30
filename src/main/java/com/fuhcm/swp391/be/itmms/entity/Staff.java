package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private EmploymentStatus status;

    @Column(name = "StartDate")
    private LocalDate startDate;
}
