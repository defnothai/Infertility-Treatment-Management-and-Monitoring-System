package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
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
@Table(name = "Application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "DateSend", nullable = false)
    private Date dateSend;

    @Column(name = "DateApproved", nullable = true)
    private Date dateApproved;

    @ManyToOne
    @JoinColumn(name = "SendBy", referencedColumnName = "Id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "ApprovedBy", referencedColumnName = "Id")
    private Account account;
}
