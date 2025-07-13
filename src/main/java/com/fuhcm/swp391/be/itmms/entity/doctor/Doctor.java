package com.fuhcm.swp391.be.itmms.entity.doctor;

import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.entity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Expertise", nullable = false, length = 255, columnDefinition = "NVARCHAR(100)")
    private String expertise;

    @Column(name = "Position", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private EmploymentStatus status;

    @Column(name = "Achievements", nullable = true, length = 255, columnDefinition = "NVARCHAR(MAX)")
    private String achievements;

    @Column(name = "Description", nullable = false, length = 255, columnDefinition = "NVARCHAR(MAX)")
    @Lob
    private String description;

    @Column(name = "Slug", nullable = false)
    private String slug;

    @Column(name = "ImgUrl", nullable = false)
    private String imgUrl;

    @OneToOne
    @JoinColumn(name = "AccountId", referencedColumnName = "Id")
    private Account account;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<DoctorReview> doctorReview;

//    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
//    private List<Application> applications;
}
