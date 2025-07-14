package com.fuhcm.swp391.be.itmms.entity;

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
@Table(name = "Report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "ReportType", nullable = false, length = 20)
    private String reportType;

    @Column(name = "Title", nullable = false, length = 20)
    private String title;

    @Column(name = "Content", nullable = false, length = 255)
    private String content;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;

    @Column(name = "CreateAt", nullable = false)
    private Date createdAt;

    @Column(name = "Resolution", nullable = false, length = 255)
    private String resolution;

    @Column(name = "ResolveAt", nullable = false)
    private Date resolvedAt;

    @ManyToOne
    @JoinColumn(name = "ResolveBy", referencedColumnName = "Id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "ReportBy", referencedColumnName = "Id")
    private User user;


}
