package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.entity.doctor.DoctorReview;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceReview;
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

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    @Size(min = 10, message = "Địa chỉ ít nhất 10 ký tự")
    @Column(name = "Address", nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String address;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh không hợp lệ")
    @Column(name = "DayOfBirth", nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Số CMND/CCCD không được để trống")
    @Size(max = 15, message = "Số CMND/CCCD tối đa 15 ký tự")
    @Size(min = 9, message = "Số CMND/CCCD ít nhất 9 ký tự")
    @Pattern(regexp = "\\d{9,15}", message = "Số CMND/CCCD chỉ được chứa chữ số")
    @Column(name = "IndentityNumber", nullable = false, length = 15)
    private String identityNumber;

    @NotBlank(message = "Quốc tịch không được để trống")
    @Size(max = 15, message = "Quốc tịch tối đa 15 ký tự")
    @Size(min = 2, message = "Quốc tịch ít nhất 2 ký tự")
    @Column(name = "Nationality", nullable = false, length = 15)
    private String nationality;

    @Size(min = 0, max = 20, message = "Số BHYT tối đa 20 chữ số")
    @Pattern(regexp = "\\d{0,20}", message = "Số BHYT chỉ được chứa chữ số")
    @Column(name = "InsuranceNumber", nullable = true, length = 20)
    private String insuranceNumber;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "AccountID", referencedColumnName = "Id")
    private Account account;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Report> reports;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Reminder> reminders;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<DoctorReview> doctorReviews;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ServiceReview> serviceReviews;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private MedicalRecord medicalRecord;
//
//    @OneToMany(mappedBy = "user")
//    @JsonIgnore
//    private List<Appointment> appointments;

}
