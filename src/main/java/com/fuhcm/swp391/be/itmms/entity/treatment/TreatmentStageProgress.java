package com.fuhcm.swp391.be.itmms.entity.treatment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TreatmentStageProgress")
public class TreatmentStageProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @Column(name = "DateStart", nullable = false)
    private Date dayStart;

    @Column(name = "DateComplete", nullable = false)
    private Date dayComplete;

    @ManyToOne
    @JoinColumn(name = "TreatmentPlanID", referencedColumnName = "Id")
    private TreatmentPlan plan;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<TreatmentSession> sessions;
}
