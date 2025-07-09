package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.dto.request.DoctorRequest;
import com.fuhcm.swp391.be.itmms.dto.response.DoctorResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.DoctorRepository;
import com.fuhcm.swp391.be.itmms.utils.SlugUtil;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;

    public List<DoctorResponse> getDoctorsInHomePage() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorResponse> responses = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (List.of(EmploymentStatus.ACTIVE, EmploymentStatus.ON_LEAVE).contains(doctor.getStatus())) {
                DoctorResponse response = modelMapper.map(doctor, DoctorResponse.class);
                response.setFullName(doctor.getAccount().getFullName());
                responses.add(response);
            }
        }
        return responses;
    }

    public DoctorResponse getDoctorById(Long id) throws NotFoundException {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bác sĩ"));
        DoctorResponse response = modelMapper.map(doctor, DoctorResponse.class);
        response.setFullName(doctor.getAccount().getFullName());
        return response;
    }

    public DoctorResponse getDoctorByEmail(String email) throws NotFoundException {
        Doctor doctor = doctorRepository.findByAccountEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bác sĩ với email"));
        return new DoctorResponse(
                doctor.getId(),
                doctor.getAccount().getFullName(),
                doctor.getExpertise(),
                doctor.getPosition(),
                doctor.getStatus(),
                doctor.getAchievements(),
                doctor.getDescription(),
                doctor.getSlug(),
                doctor.getImgUrl()
        );
    }

    @Transactional
    public DoctorResponse updateDoctor(Long doctorId, DoctorRequest request) throws NotFoundException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin bác sĩ"));
        modelMapper.map(request, doctor);
        doctor.setSlug(SlugUtil.toSlug(doctor.getAccount().getFullName() + "-" + doctor.getPosition() + "-" + doctor.getExpertise()));
        doctorRepository.save(doctor);
        return new DoctorResponse(
                doctor.getId(),
                doctor.getAccount().getFullName(),
                doctor.getExpertise(),
                doctor.getPosition(),
                doctor.getStatus(),
                doctor.getAchievements(),
                doctor.getDescription(),
                doctor.getSlug(),
                doctor.getImgUrl()
        );
    }

    public DoctorResponse createDoctor(DoctorRequest request) throws NotFoundException {
        Account account = accountRepository.findByEmail(request.getEmail());
        if (account == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với email: " + request.getEmail());
        }
        Doctor doctor = modelMapper.map(request, Doctor.class);
        doctor.setAccount(account);
        doctor.setSlug(SlugUtil.toSlug(account.getFullName() + "-" + request.getPosition() + "-" + request.getExpertise()));
        doctor = doctorRepository.save(doctor);
        return new DoctorResponse(
                doctor.getId(),
                account.getFullName(),
                doctor.getExpertise(),
                doctor.getPosition(),
                doctor.getStatus(),
                doctor.getAchievements(),
                doctor.getDescription(),
                doctor.getSlug(),
                doctor.getImgUrl()
        );
    }







}
