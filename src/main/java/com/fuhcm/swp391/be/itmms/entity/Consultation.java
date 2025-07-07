package com.fuhcm.swp391.be.itmms.entity;

import com.fuhcm.swp391.be.itmms.constant.ConsultationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Column(name = "PatientName", nullable = false, length = 100)
    @Nationalized
    private String patientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "PhoneNumber", nullable = false, length = 12)
    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Column(name = "Email", nullable = false, length = 30)
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Lob
    @Column(name = "Message", nullable = true)
    @Nationalized
    private String msg;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private ConsultationStatus status;

    @ManyToOne
    @JoinColumn(name = "Handler", referencedColumnName = "Id")
    private Account staff;
}
