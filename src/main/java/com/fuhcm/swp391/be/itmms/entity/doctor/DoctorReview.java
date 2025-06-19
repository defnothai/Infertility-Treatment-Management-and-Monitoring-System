package com.fuhcm.swp391.be.itmms.entity.doctor;

import com.fuhcm.swp391.be.itmms.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DoctorReview")
public class DoctorReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Point", nullable = false)
    private int point;

    @Column(name = "Content", nullable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "DoctorID", referencedColumnName = "Id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "Id")
    private User user;
}
