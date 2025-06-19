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
@Table(name = "Reminder")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Column(name = "Description", nullable = false, length = 255)
    private String description;

    @Column(name = "RemindDate", nullable = false)
    private Date remindDate;

    @Column(name = "IsComplete", nullable = false, length = 3)
    private String isComplete;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "Id")
    private User user;
}
