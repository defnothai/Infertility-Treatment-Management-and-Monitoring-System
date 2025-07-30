package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.entity.doctor.DoctorReview;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceReview;
import com.fuhcm.swp391.be.itmms.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Địa chỉ không được để trống", groups = OnUpdate.class)
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự", groups = OnUpdate.class)
    @Size(min = 10, message = "Địa chỉ ít nhất 10 ký tự" , groups = OnUpdate.class)
    @Column(name = "Address", nullable = true, length = 255, columnDefinition = "NVARCHAR(255)")
    private String address;

    @NotNull(message = "Ngày sinh không được để trống" , groups = OnUpdate.class)
    @Past(message = "Ngày sinh không hợp lệ" , groups = OnUpdate.class)
    @Column(name = "DayOfBirth", nullable = true)
    private LocalDate dob;

    @Size(max = 15, message = "Số CMND/CCCD tối đa 15 ký tự", groups = OnUpdate.class)
    @Size(min = 9, message = "Số CMND/CCCD ít nhất 9 ký tự", groups = OnUpdate.class)
    @Pattern(regexp = "\\d{9,15}", message = "Số CMND/CCCD chỉ được chứa chữ số", groups = OnUpdate.class)
    @Column(name = "IdentityNumber", nullable = true, length = 15)
    private String identityNumber;

    @NotBlank(message = "Quốc tịch không được để trống" , groups = OnUpdate.class)
    @Size(max = 15, message = "Quốc tịch tối đa 15 ký tự", groups = OnUpdate.class)
    @Size(min = 2, message = "Quốc tịch ít nhất 2 ký tự", groups = OnUpdate.class)
    @Column(name = "Nationality", nullable = true, length = 15, columnDefinition = "NVARCHAR(50)")
    private String nationality;

    @Size(min = 0, max = 20, message = "Số BHYT tối đa 20 chữ số", groups = OnUpdate.class)
    @Pattern(regexp = "\\d{0,20}", message = "Số BHYT chỉ được chứa chữ số", groups = OnUpdate.class)
    @Column(name = "InsuranceNumber", nullable = true, length = 20)
    private String insuranceNumber;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "AccountID", referencedColumnName = "Id")
    private Account account;

    public User(String address, LocalDate dob, String identityNumber, String nationality, String insuranceNumber, Account account) {
        this.address = address;
        this.dob = dob;
        this.identityNumber = identityNumber;
        this.nationality = nationality;
        this.insuranceNumber = insuranceNumber;
        this.account = account;
    }

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Report> reports;

//    @OneToMany(mappedBy = "user")
//    @JsonIgnore
//    private List<Reminder> reminders;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<DoctorReview> doctorReviews;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ServiceReview> serviceReviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MedicalRecord> medicalRecords;

//
//    @OneToMany(mappedBy = "user")
//    @JsonIgnore
//    private List<Appointment> appointments;

}
