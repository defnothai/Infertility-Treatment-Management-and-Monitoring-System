package com.fuhcm.swp391.be.itmms.entity.service;

import com.fuhcm.swp391.be.itmms.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "ServiceReview")
public class ServiceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Point", nullable = false)
    private int point;

    @Column(name = "Content", nullable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "PatientID", referencedColumnName = "Id")
    private User user;
}
