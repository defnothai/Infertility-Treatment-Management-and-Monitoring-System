package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "HospitalAchievement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Title", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    @Lob
    private String description;

    @Column(name = "AchievedAt", nullable = false)
    private LocalDate achievedAt;

    @Column(name = "ImgUrl", nullable = false)
    @Lob
    private String imgUrl;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive;

    @Column(name = "Slug", nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", nullable = false)
    private Account createdBy;
}

