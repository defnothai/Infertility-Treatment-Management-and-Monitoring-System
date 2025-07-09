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

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    @Lob
    private String description;

    @Column(name = "AchievedAt")
    private LocalDate achievedAt;

    @Column(name = "ImgUrl")
    @Lob
    private String imgUrl;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;

    @Column(name = "Slug", nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy")
    private Account createdBy;
}

