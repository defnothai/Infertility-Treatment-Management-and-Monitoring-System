package com.fuhcm.swp391.be.itmms.controller;


import com.fuhcm.swp391.be.itmms.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")

public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/api/home/doctors")
    public ResponseEntity getDoctorsInHomePage() {
        return doctorService.getDoctorsInHomePage();
    }
}
