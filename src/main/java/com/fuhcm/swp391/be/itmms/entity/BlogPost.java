package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.BlogStatus;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
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

    @Column(name = "Title", nullable = false, length = 1000)
    @Nationalized
    private String title;

    @Lob
    @Column(name = "Content", nullable = false)
    @Nationalized
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDate createdAt;

    @Column(name = "Status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @Column(name = "HandledAt", nullable = true)
    private LocalDate handleAt;

    @Column(name = "DeletedAt", nullable = true)
    private LocalDate deletedAt;

    @ManyToOne
    @JoinColumn(name = "DeletedBy", referencedColumnName = "Id")
    private Account deletedBy;

    @ManyToOne
    @JoinColumn(name = "Handler", referencedColumnName = "Id")
    private Account handler;

    @ManyToOne
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private Account createdBy;


}
