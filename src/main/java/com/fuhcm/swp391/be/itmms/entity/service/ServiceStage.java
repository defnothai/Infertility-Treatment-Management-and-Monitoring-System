package com.fuhcm.swp391.be.itmms.entity.service;

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
@Table(name = "ServiceStage")
public class ServiceStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Column(name = "StageOrder", nullable = false)
    private int stageOrder;

    @Column(name = "Duration", nullable = false)
    private int duration;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "Id")
    private Service service;
}
