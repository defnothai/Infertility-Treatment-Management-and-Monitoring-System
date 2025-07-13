package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.ReminderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private LocalDateTime remindDate;

    @Column(name = "IsSent", nullable = false)
    private boolean isSent;

    @Column(name = "ReminderType", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ReminderType reminderType;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "Id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
