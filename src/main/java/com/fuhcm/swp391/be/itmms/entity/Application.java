package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.ApplicationStatus;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title", nullable = false)
    private String title;

    @Lob
    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "DateSend", nullable = false)
    private LocalDate dateSend;

    @Column(name = "DateHandled", nullable = true)
    private LocalDate dateHandled;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "LeaveDate")
    private LocalDate leaveDate;

    @ManyToOne
    @JoinColumn(name = "SendBy", referencedColumnName = "Id")
    private Account doctor;

    @ManyToOne
    @JoinColumn(name = "Handler", referencedColumnName = "Id")
    private Account account;
}
