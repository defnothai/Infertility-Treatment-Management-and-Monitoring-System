    package com.fuhcm.swp391.be.itmms.entity.service;

    import com.fuhcm.swp391.be.itmms.entity.Account;
    import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.List;

    @Entity
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "Service")
    public class Service {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "Id")
        private int id;

        @Column(name = "ServiceName", nullable = false, columnDefinition = "NVARCHAR(100)")
        private String serviceName;

        @Column(name = "SubTitle", nullable = false, columnDefinition = "NVARCHAR(100)")
        private String subTitle;

        @Column(name = "Price", nullable = false)
        private double price;

        @Column(name = "Summary", nullable = false, columnDefinition = "NVARCHAR(MAX)")
        @Lob
        private String summary;

        @Column(name = "Slug", nullable = false)
        private String slug;

        @Column(name = "ImgUrl", nullable = false)
        private String imgUrl;

        @ManyToOne
        @JoinColumn(name = "ManageBy", referencedColumnName = "Id")
        private Account account;

        @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
        private List<ServiceStage> stage;

        @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
        private List<ServiceReview> reviews;

        @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
        private List<ServiceDetails> details;

        @OneToMany(mappedBy = "service")
        private List<TreatmentPlan> plans;
    }
