package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BlogPost")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title", nullable = false, length = 255)
    private String title;

    @Column(name = "Content", nullable = false, length = 255)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private Date createdAt;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;

    @Column(name = "ApprovedAt", nullable = false)
    private Date approvedAt;

    @ManyToOne
    @JoinColumn(name = "ApprovedBy", referencedColumnName = "Id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private Doctor doctor;


}
