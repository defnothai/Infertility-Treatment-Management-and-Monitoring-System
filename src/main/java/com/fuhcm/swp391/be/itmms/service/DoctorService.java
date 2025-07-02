package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.DoctorResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ManagerInfo;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AuthenticationService authenticationService;

    public DoctorService(DoctorRepository doctorRepository,
                         AuthenticationService authenticationService) {
        this.doctorRepository = doctorRepository;
        this.authenticationService = authenticationService;
    }

    public List<DoctorResponse> getDoctorsInHomePage() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorResponse> doctorResponses = new ArrayList<>();
        DoctorResponse doctorResponse;
        for (Doctor doctor : doctors) {
            String fullName = doctor.getAccount().getFullName();
            String position = doctor.getPosition();
            String achivement = doctor.getAchivements();
            String imgUrl = doctor.getImgUrl();
            String slug = doctor.getSlug();
            doctorResponses.add(new DoctorResponse(fullName, position, achivement, imgUrl, slug));
        }
        return doctorResponses;
    }

    public List<ManagerInfo> getManagerInfo() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<ManagerInfo> managers = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor.getAccount().getRoles().contains("ROLE_MANAGER") &&
                    List.of("ACTIVE", "ON_LEAVE").contains(doctor.getStatus())) {
                managers.add(new ManagerInfo(doctor.getAccount().getFullName(),
                                             doctor.getAccount().getEmail(),
                                             doctor.getPosition()));
            }
        }
        return managers;
    }

    public ManagerInfo getCurrentManagerInfo(String email) {
        Account account = authenticationService.findByEmail(email);
        Doctor doctor = doctorRepository.findByAccount(account);
        return new ManagerInfo(account.getFullName(),
                               account.getEmail(),
                               doctor.getPosition());
    }

    public Object createMedicalRecord() {
        return null;
    }
}
