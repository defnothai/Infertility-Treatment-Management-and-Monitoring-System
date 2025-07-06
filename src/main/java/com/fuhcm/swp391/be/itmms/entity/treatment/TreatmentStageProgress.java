package com.fuhcm.swp391.be.itmms.entity.treatment;

import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @Enumerated(EnumType.STRING)
    private TreatmentStageStatus status;

    @Column(name = "Notes", nullable = true)
    private String notes;

    @Column(name = "DateStart", nullable = true)
    private LocalDate dayStart;

    @Column(name = "DateComplete", nullable = true)
    private LocalDate dayComplete;


    @ManyToOne
    @JoinColumn(name = "TreatmentPlanID", referencedColumnName = "Id")
    private TreatmentPlan plan;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<TreatmentSession> sessions;

    @ManyToOne
    @JoinColumn(name = "ServiceStageID", referencedColumnName = "Id")
    private ServiceStage serviceStage;
}
