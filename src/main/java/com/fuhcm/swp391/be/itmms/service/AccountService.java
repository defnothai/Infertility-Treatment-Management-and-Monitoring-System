package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.security.PasswordEncoder;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.constant.AppointmentStatus;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.dto.PatientInfo;
import com.fuhcm.swp391.be.itmms.dto.request.AccountCreateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.*;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
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


    public void register(Account account) {
        accountRepo.save(account);
    }
    public Account updateAccount(Account account) {return accountRepo.save(account);}

    public List<PatientInfo> getPatientInfo() throws NotFoundException {
        Role userRole = roleService.findByRoleName(AccountRole.ROLE_USER);
        List<Account> patients = accountRepo.findByRoles(List.of(userRole));

        if (patients.isEmpty()) {
            throw new NotFoundException("Danh sách bệnh nhân trống");
        }

        return patients.stream()
                .map(acc -> modelMapper.map(acc, PatientInfo.class))
                .collect(Collectors.toList());
    }

    public Account findById(Long id) throws NotFoundException {
        return accountRepo.findById(id).orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
    }

    public List<PatientInfo> searchPatientByPhoneNumber(String phoneNumber) throws NotFoundException {
        List<Account> accounts = accountRepo.findByPhoneNumberContaining(phoneNumber);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bệnh nhân");
        }
        return accounts.stream()
                .map(account -> modelMapper.map(account, PatientInfo.class))
                .collect(Collectors.toList());
    }

    public List<PatientInfo> searchPatientByEmail(String email) throws NotFoundException {
        List<Account> accounts = accountRepo.findByEmailContaining(email);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bệnh nhân");
        }
        return accounts.stream()
                .map(account -> modelMapper.map(account, PatientInfo.class))
                .collect(Collectors.toList());
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
}
