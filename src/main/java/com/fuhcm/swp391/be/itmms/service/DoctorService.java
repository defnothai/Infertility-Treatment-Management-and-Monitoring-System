package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.DoctorResponse;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public ResponseEntity getDoctorsInHomePage() {
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
        return ResponseEntity.ok(doctorResponses);
    }
}
