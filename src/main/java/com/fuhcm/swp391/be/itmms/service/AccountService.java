package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.PasswordEncoder;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.dto.request.AccountCreateRequest;
import com.fuhcm.swp391.be.itmms.dto.DirectPatientDTO;
import com.fuhcm.swp391.be.itmms.dto.request.ProfileUpdateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService {

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleService roleService;

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    @Lazy
    private ScheduleService scheduleService;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private ShiftService shiftService;
    @Autowired
    @Lazy
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;


    public DirectPatientDTO createDirectPatient(DirectPatientDTO request) {
        Account staff = authenticationService.getCurrentAccount();
        // account
        Account account = modelMapper.map(request, Account.class);
        account.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(request.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(AccountStatus.ENABLED);
        account.setRoles(Collections.singletonList(roleService.findByRoleName(AccountRole.ROLE_USER)));
        account.setCreatedBy(staff);
        accountRepo.save(account);
        // user
        User user = modelMapper.map(request, User.class);
        user.setAccount(account);
        userRepository.save(user);
        // email
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(account.getEmail());
        emailDetail.setSubject("CHÀO MỪNG BẠN ĐẾN VỚI BỆNH VIỆN THÀNH NHÂN");
        emailDetail.setFullName(account.getFullName());
        emailDetail.setPassword(request.getPassword());
        emailDetail.setLink("localhost:3000");
        emailService.sendDirectPatientAccountEmail(emailDetail);
        //
        DirectPatientDTO dto = new DirectPatientDTO();
        modelMapper.map(account, dto);
        modelMapper.map(user, dto);
        return dto;
    }

    public List<DirectPatientDTO> getDirectPatientsByCurrentStaff() {
        Account currentStaff = authenticationService.getCurrentAccount();
        List<Account> createdAccounts = accountRepo.findAllByCreatedByOrderByCreatedAtDesc(currentStaff);
        List<DirectPatientDTO> patientList = new ArrayList<>();
        for (Account account : createdAccounts) {
            DirectPatientDTO dto = new DirectPatientDTO();
            modelMapper.map(account, dto);
            if (account.getUser() != null) {
                modelMapper.map(account.getUser(), dto);
            }
            patientList.add(dto);
        }
        return patientList;
    }



    public void register(Account account) {
        accountRepo.save(account);
    }
    public Account updateAccount(Account account) {return accountRepo.save(account);}


    public Account findById(Long id) throws NotFoundException {
        return accountRepo.findById(id).orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
    }


    public List<AccountBasic> getManagerAccount() {
        return accountRepo
                .findByRoles(List.of(roleService.findByRoleName(AccountRole.ROLE_MANAGER)))
                .stream()
                .map(account -> modelMapper.map(account, AccountBasic.class))
                .collect(Collectors.toList());
    }

    public List<AccountBasic> getDoctorAccounts() {
        List<Account> doctors = accountRepo.findByRoles(List.of(roleService.findByRoleName(AccountRole.ROLE_DOCTOR)));
        return doctors.stream()
                .map(AccountBasic::new)
                .collect(Collectors.toList());
    }

    public List<AccountBasic> searchDoctors(String keyword) throws NotFoundException {
        String lowerKeyword = keyword.trim().toLowerCase();
        return getDoctorAccounts().stream()
                .filter(account -> account.getFullName().toLowerCase().contains(lowerKeyword)
                        || account.getEmail().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }


    public ProfileResponse getUserProfile(Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        if(account == null) {
            return null;
        } else if(account.getRoles().getFirst().getRoleName().equals(AccountRole.ROLE_USER)) {
            User user = account.getUser();
            return new UserProfileResponse(
                    account.getFullName(),
                    account.getEmail(),
                    account.getPhoneNumber(),
                    user.getDob(),
                    account.getGender().toString(),
                    user.getIdentityNumber(),
                    user.getNationality(),
                    user.getInsuranceNumber(),
                    user.getAddress()
            );
        } else if(account.getRoles().getFirst().getRoleName().equals(AccountRole.ROLE_DOCTOR)) {
            Doctor doctor = account.getDoctor();
            return new DoctorProfileResponse(
                    account.getFullName(),
                    account.getEmail(),
                    account.getPhoneNumber(),
                    doctor.getExpertise(),
                    doctor.getPosition(),
                    doctor.getAchievements()
            );
        } else {
            return new StaffProfileResponse(
                    account.getFullName(),
                    account.getEmail(),
                    account.getPhoneNumber()
            );
        }
    }

    public Account getAuthenticatedUser(@Valid Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        if(account == null){
            throw new IllegalArgumentException();
        }
        return account;
    }

    public Account resolveDoctor(Long doctorId, LocalDate workDate, LocalTime desired) {
        Account account;
        if(doctorId != null){
            Optional<Account> accountOpt = accountRepo.findById(doctorId);
            if(accountOpt.isPresent()){
                return accountOpt.get();
            } else {
                throw new IllegalArgumentException("Doctor not found");
            }
        }
        List<Schedule> schedules = scheduleRepo.findByWorkDate(workDate);
        Map<Account, Integer> availableDoctors = new HashMap<>();
        for (Schedule schedule : schedules) {
            Account doctor = schedule.getAssignTo();
            List<LocalTime> availableSlots = scheduleService.getAvailableSlots(doctor.getId(), workDate);
            if(availableSlots.contains(desired)){
                int count = appointmentRepo.findByDoctorIdAndTime(doctor.getId(), workDate).size();
                availableDoctors.put(doctor, count);
            }
        }
        if(availableDoctors.isEmpty()){
            throw new IllegalArgumentException("No available doctor");
        }
        Account selector = null;
        int minAppointmentCount = Integer.MAX_VALUE;
        for(Map.Entry<Account, Integer> entry : availableDoctors.entrySet()){
            if(entry.getValue() < minAppointmentCount){
                minAppointmentCount = entry.getValue();
                selector = entry.getKey();
            }
        }
        return selector;
    }

    public List<AccountResponse> getAllAccounts() {
        List<AccountResponse> responses = new ArrayList<>();
        List<Account> accounts = accountRepo.findAll();
        for (Account account : accounts) {
            if(account.getRoles().getFirst().getRoleName().equals(AccountRole.ROLE_ADMIN)){
                continue;
            }
            responses.add(new AccountResponse(account));
        }
        return responses;
    }

    public Account createNewAccount(AccountCreateRequest request, Authentication authentication) {
        Account account = new Account();
        Account createdBy = accountRepo.findByEmail(authentication.getName());
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

    public boolean deleteAccount(Long id) {
        Account account =  accountRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(account == null) return false;
        account.setStatus(AccountStatus.DELETED);
        accountRepo.save(account);
        return true;
    }

    public Set<AccountResponse> getAvailableDoctors() {
        LocalDate current = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusMonths(1).withDayOfMonth(current.plusMonths(1).lengthOfMonth());
        Set<AccountResponse> responses = new HashSet<>();
        List<Schedule> allSchedules = scheduleRepo.findByWorkDateBetween(current, toDate);
        for (Schedule schedule : allSchedules) {
            Long doctorId = schedule.getAssignTo().getId();
            Doctor doctor = doctorRepo.findByAccountIdAndStatus(doctorId, EmploymentStatus.ACTIVE);
            if(doctor != null){
                Shift shift = schedule.getShift();
                List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
                List<Appointment> appointments = appointmentRepo.findByDoctorIdAndTimeAndStatusNot(doctorId, schedule.getWorkDate(), AppointmentStatus.NOT_PAID);
                Set<LocalTime> booked = new HashSet<>();
                for(Appointment appointment : appointments){
                    if(appointment.getStartTime() != null){
                        booked.add(appointment.getStartTime());
                    }
                }
                for(LocalTime slotTime : allSlots){
                    if(!booked.contains(slotTime)){
                        responses.add(new AccountResponse(doctorId, schedule.getAssignTo().getFullName()));
                    }
                }
            }
        }
        return responses;
    }

    public List<AccountReportResponse> getAccountReport(@Valid @NotNull LocalDate fromDate,
                                                  @Valid @NotNull LocalDate toDate) {
        List<AccountReportResponse> responses = new ArrayList<>();
        List<Account> accounts = accountRepo.findByCreatedAtBetween(fromDate.atStartOfDay(), toDate.atStartOfDay().plusDays(1));
        LocalDate current = fromDate;
        while(!current.isAfter(toDate)){
            List<Account> enabledAccounts = new ArrayList<>();
            List<Account> disabledAccounts = new ArrayList<>();
            List<Account> deletedAccounts = new ArrayList<>();
            for(Account account : accounts){
                if(account.getCreatedAt().toLocalDate().equals(current)){
                    if(account.getStatus().name().equalsIgnoreCase(AccountStatus.DISABLED.name())){
                        disabledAccounts.add(account);
                    } else if(account.getStatus().name().equalsIgnoreCase(AccountStatus.ENABLED.name())){
                        enabledAccounts.add(account);
                    } else if(account.getStatus().name().equalsIgnoreCase(AccountStatus.DELETED.name())){
                        deletedAccounts.add(account);
                    }
                }
            }
            AccountReportResponse accountReportResponse = new AccountReportResponse();
            accountReportResponse.setEnabledAccount(enabledAccounts.size());
            accountReportResponse.setDisabledAccount( disabledAccounts.size());
            accountReportResponse.setDeletedAccount(deletedAccounts.size());
            accountReportResponse.setTotalAccount(enabledAccounts.size() +  disabledAccounts.size() + deletedAccounts.size());
            accountReportResponse.setDate(current);
            responses.add(accountReportResponse);
            current = current.plusDays(1);
        }


        return responses;
    }

    public ProfileResponse updateUserProfile(ProfileUpdateRequest request) {
        Account account = authenticationService.getCurrentAccount();
        account.setFullName(request.getFullName());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setGender(request.getGender());
        User user = account.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setDob(request.getDob());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setNationality(request.getNationality());
        user.setInsuranceNumber(request.getInsuranceNumber());
        user.setAddress(request.getAddress());
        user.setAccount(account);
        userRepository.save(user);
        account = accountRepo.save(account);
        return new UserProfileResponse(
                account.getFullName(),
                account.getEmail(),
                account.getPhoneNumber(),
                user.getDob(),
                account.getGender() == null ? null : account.getGender().toString(),
                user.getIdentityNumber(),
                user.getNationality(),
                user.getInsuranceNumber(),
                user.getAddress()
        );
    }

}


