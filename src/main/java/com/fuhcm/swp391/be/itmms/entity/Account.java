package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.entity.invoice.Invoice;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTestResult;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import com.fuhcm.swp391.be.itmms.entity.service.Service;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 10, message = "Họ tên ít nhất 10 ký tự")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    @Column(name = "FullName", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(name = "Email", nullable = false, length = 50, unique = true)
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Size(max = 50, message = "Email tối đa 50 ký tự")
    private String email;


    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự")
    @Column(name = "Password", nullable = false, length = 255, columnDefinition = "NVARCHAR(100)")
    private String password;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Số điện thoại không hợp lệ")
    @Column(name = "PhoneNumber", nullable = true, length = 15, unique = true)
    private String phoneNumber;

    @Column(name = "Gender", nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy")
    private Account createdBy;

    @OneToOne(mappedBy = "account")
    @JsonIgnore
    private User user;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(
            name = "AccountRoles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "handler")
    @JsonIgnore
    private List<BlogPost> blogPost;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<BlogPost> blogPosts;

    @OneToMany(mappedBy = "deletedBy")
    @JsonIgnore
    private List<BlogPost> blogPosts1;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Report> report;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Service> services;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<MedicalRecordAccess> medicalRecordAccess;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Application> applications;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<LabTestResult> labTestResults;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ScheduleTemplate>  scheduleTemplates;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    private List<Consultation>  consultations;

    @OneToOne(mappedBy = "account")
    @JsonIgnore
    private Doctor doctor;
  
    @OneToMany(mappedBy = "doctor")
    private List<Ultrasound> ultrasounds;

    @OneToMany(mappedBy = "createdBy")
    private List<HospitalAchievement> achievements;
}
