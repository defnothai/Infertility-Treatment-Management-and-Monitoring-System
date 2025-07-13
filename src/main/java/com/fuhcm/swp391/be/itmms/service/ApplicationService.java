package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ApplicationStatus;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.dto.request.ApplicationRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ApplicationResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Application;
import com.fuhcm.swp391.be.itmms.entity.Role;
import com.fuhcm.swp391.be.itmms.entity.Staff;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ApplicationRepository;
import com.fuhcm.swp391.be.itmms.repository.DoctorRepository;
import com.fuhcm.swp391.be.itmms.repository.StaffRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;
    public ApplicationResponse createApplication(@Valid ApplicationRequest applicationRequest,
                                                 Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName());
        if(account == null){
            throw new RuntimeException("Account not found");
        }
        Application application = new Application();
        application.setTitle(applicationRequest.getTitle());
        application.setDescription(applicationRequest.getDescription());
        application.setDateSend(LocalDate.now());
        application.setAccount(account);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDoctor(null);
        application.setLeaveDate(applicationRequest.getDate());
        applicationRepository.save(application);
        return new ApplicationResponse(application);
    }

    public List<ApplicationResponse> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for(Application application : applications){
            applicationResponses.add(new ApplicationResponse(application));
        }
        return applicationResponses;
     }

    public ApplicationResponse updateApplication(@Valid String status, @Valid Long id) {
        Application application =  applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if(status.equalsIgnoreCase(ApplicationStatus.APPROVED.toString())){
            application.setStatus(ApplicationStatus.APPROVED);
            Role role = application.getDoctor().getRoles().get(0);
            if(role.getRoleName().equals("ROLE_DOCTOR")){
                Doctor doctor = doctorRepository.findByAccount(application.getDoctor());
                doctor.setStatus(EmploymentStatus.ON_LEAVE);
                doctorRepository.save(doctor);
            } else if(role.getRoleName().equals("ROLE_STAFF")){
                Staff staff = staffRepository.findByAccount(application.getDoctor());
                staff.setStatus(EmploymentStatus.ON_LEAVE);
                staffRepository.save(staff);
            }
        } else if(status.equalsIgnoreCase(ApplicationStatus.REJECTED.toString())){
            application.setStatus(ApplicationStatus.REJECTED);
        }
        applicationRepository.save(application);
        return new ApplicationResponse(application);
    }

    public List<ApplicationResponse> getMineApplications(Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName());
        if(account == null){
            throw new RuntimeException("Account not found");
        }
        List<Application> applications = applicationRepository.findByDoctor(account);
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for(Application application : applications){
            applicationResponses.add(new ApplicationResponse(application));
        }
        return applicationResponses;
    }
}
