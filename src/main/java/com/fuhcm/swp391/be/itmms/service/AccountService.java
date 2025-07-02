package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.PasswordEncoder;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import com.fuhcm.swp391.be.itmms.dto.DoctorAvailability;
import com.fuhcm.swp391.be.itmms.dto.request.AccountCreateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AccountResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ProfileResponse;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.AppointmentRepository;
import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepo.findAll();
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (Account account : accounts) {
            long id = account.getId();
            String fullName = account.getFullName();
            String email = account.getEmail();
            LocalDateTime createdAt = account.getCreatedAt();
            AccountStatus status = account.getStatus();
            String phoneNumber = account.getPhoneNumber();
            Gender gender = account.getGender();
            Account createdBy = account.getCreatedBy();
            accountResponses.add(new AccountResponse(id, fullName, email, createdAt, status, phoneNumber, gender, createdBy ));
        }
        return accountResponses;
    }

    public void register(Account account) {
        accountRepo.save(account);
    }

    public boolean deleteAccount(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid account ID: must be a positive number");
        }
        Account account = accountRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(account == null){
            return false;
        }
        account.setStatus(AccountStatus.DELETED);
        accountRepo.save(account);
        return true;
    }

    public Account createNewAccount(@Valid AccountCreateRequest request) {
        Account account = new Account();
        Account createdBy = accountRepo.findById(request.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("CreatedBy account not found"));
        Role role = roleRepo.findByRoleName(request.getRoles())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + request.getRoles()));
        account.setFullName(request.getFullName());
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(request.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setGender(request.getGender());
        account.setStatus(request.getStatus());
        account.setCreatedBy(createdBy);
        account.setRoles(Collections.singletonList(role));
        return accountRepo.save(account);
    }

    public ProfileResponse getProfile(@Valid Authentication authentication) {
        Optional<Account> accountOpt = accountRepo.findByEmail(authentication.getName());

        Account account = accountOpt.get();

        ProfileResponse  profileResponse = new ProfileResponse();
        User user = account.getUser();
        profileResponse.setUserName(account.getFullName());
        profileResponse.setGender(account.getGender());
        if(user != null){
            profileResponse.setIdentityNumber(account.getUser().getIdentityNumber());
            profileResponse.setNationality(account.getUser().getNationality());
            profileResponse.setInsuranceNumber(account.getUser().getInsuranceNumber());
            profileResponse.setRole(account.getRoles().getFirst().getAuthority());
            profileResponse.setAddress(account.getUser().getAddress());
        } else {
            profileResponse.setIdentityNumber(null);
            profileResponse.setNationality(null);
            profileResponse.setInsuranceNumber(null);
            profileResponse.setAddress(null);
            profileResponse.setRole(account.getRoles().getFirst().getAuthority());
        }
        return profileResponse;
    }

    public Set<AccountResponse> getAvailableDoctors() {
        LocalDate current = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusMonths(1).withDayOfMonth(current.plusMonths(1).lengthOfMonth());
        Set<AccountResponse> accountResponses = new HashSet<>();
        List<Schedule> allSchedules = scheduleRepo.findByWorkDateBetween(current, toDate);
        for(Schedule schedule : allSchedules){
            Long doctorId = schedule.getAssignTo().getId();
            Shift shift = schedule.getShift();
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepo.findByDoctorIdAndTime(doctorId, schedule.getWorkDate());
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment  appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime)){
                    accountResponses.add(new AccountResponse(doctorId, schedule.getAssignTo().getFullName()));
                }
            }
        }
        return accountResponses;
    }

    public Account getAuthenticatedUser(@Valid Authentication authentication) {
        return accountRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public Account resolveDoctor(Long doctorId, LocalDate workDate, LocalTime desiredTime) {
        if(doctorId != null){
            return accountRepo.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        }

        List<Schedule> schedules = scheduleRepo.findByWorkDate(workDate);
        Map<Account, Integer> availableDoctors = new HashMap<>();
        for(Schedule schedule : schedules){
            Account doctor = schedule.getAssignTo();
            List<LocalTime> availableSlots = scheduleService.getAvailableSlots(doctor.getId(), workDate);
            if(availableSlots.contains(desiredTime)){
                int count = appointmentRepo.findByDoctorIdAndTime(doctor.getId(), workDate).size();
                availableDoctors.put(doctor, count);
            }
        }
        if(availableDoctors.isEmpty()){
            throw new IllegalArgumentException("No doctor available");
        }
        Account selectDoctor = null;
        int minAppointmentCount = Integer.MAX_VALUE;
        for (Map.Entry<Account, Integer> entry : availableDoctors.entrySet()) {
            if (entry.getValue() < minAppointmentCount) {
                minAppointmentCount = entry.getValue();
                selectDoctor = entry.getKey();
            }
        }
        return  selectDoctor;
    }
}