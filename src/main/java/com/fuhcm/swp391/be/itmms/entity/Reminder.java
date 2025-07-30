package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.ReminderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "Reminder")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private LocalDateTime remindAt;

    private boolean isSent;

    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private ReminderType type; // EMAIL, WEB_NOTIFICATION

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    public Reminder(String content, LocalDateTime remindAt, ReminderType type, Appointment appointment) {
        this.content = content;
        this.remindAt = remindAt;
        this.type = type;
        this.appointment = appointment;
    }
}

