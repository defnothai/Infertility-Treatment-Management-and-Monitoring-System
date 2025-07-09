package com.fuhcm.swp391.be.itmms.controller;


import com.fuhcm.swp391.be.itmms.dto.request.DoctorRequest;
import com.fuhcm.swp391.be.itmms.dto.response.DoctorResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.DoctorService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/api/home/doctors")
    public ResponseEntity getDoctorsInHomePage() {
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                                            "FETCH_SUCCESS",
                                        "Lấy dữ liệu thành công",
                                                doctorService.getDoctorsInHomePage())
        );

    }

    @GetMapping("/api/doctors/{id}")
    public ResponseEntity getDoctorById(@PathVariable Long id) throws NotFoundException {
        DoctorResponse response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_SUCCESS",
                "Lấy thông tin bác sĩ thành công",
                response));
    }

    @GetMapping("/api/manage/doctors/details")
    public ResponseEntity<?> getDoctorByEmail(@RequestParam String email) throws NotFoundException {
        DoctorResponse doctor = doctorService.getDoctorByEmail(email);
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy thông tin bác sĩ thành công",
                        doctor)
        );
    }

    @PutMapping("/api/manage/doctors/details/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable("id") Long doctorId,
                                          @RequestBody DoctorRequest request) throws NotFoundException {
        DoctorResponse response = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật thông tin bác sĩ thành công",
                        response)
        );
    }








}
