package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.EmploymentStatus;
import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.dto.request.ScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.request.WeeklyScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AccountResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleResponse;
import com.fuhcm.swp391.be.itmms.dto.response.SuggestionResponse;
import com.fuhcm.swp391.be.itmms.entity.*;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ScheduleTemplateRepository templateRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

//    public List<ScheduleResponse> getSchedulesByWeekYear(WeeklyScheduleRequest request, Authentication authentication) {
//        Account currentUser = accountRepository.findByEmail(authentication.getName());
//
//        String[] parts = request.getWeekly().split(" to ");
//        String[] startParts = parts[0].split("/");
//        String[] endParts = parts[1].split("/");
//
//        int year = request.getYear();
//        int startDay = Integer.parseInt(startParts[0]);
//        int startMonth = Integer.parseInt(startParts[1]);
//        int endDay = Integer.parseInt(endParts[0]);
//        int endMonth = Integer.parseInt(endParts[1]);
//
//        LocalDate startDate = LocalDate.of(year, startMonth, startDay);
//        LocalDate endDate = LocalDate.of(year, endMonth, endDay);
//
//        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(
//                currentUser.getId(), startDate, endDate);
//        return schedules.stream().map(schedule -> {
//            ScheduleResponse dto = modelMapper.map(schedule, ScheduleResponse.class);
//            dto.setShiftTime(schedule.getShift().getStartTime() + " - " + schedule.getShift().getEndTime());
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//
//    @Transactional
//    public void generateStaffSchedules(LocalDate startDate,
//                                       LocalDate endDate,
//                                       Authentication authentication) throws BadRequestException {
//        if (startDate.isAfter(endDate)) {
//            throw new BadRequestException("Thời gian nhập không hợp lệ");
//        }
//        List<ScheduleTemplate> templates = scheduleTemplateRepository.findAll();
//        Account account = accountRepository.findByEmail(authentication.getName());
//
//        for (ScheduleTemplate template : templates) {
//            LocalDate scheduleStartDate = startDate;
//            while (!scheduleStartDate.isAfter(endDate)) {
//                if (scheduleStartDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
//                    boolean exists = scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
//                            template.getAccount().getId(), template.getShift().getId().intValue(), scheduleStartDate);
//                    if (!exists) {
//                        Schedule schedule = new Schedule();
//                        schedule.setWorkDate(scheduleStartDate);
//                        schedule.setRoomNumber(template.getRoomNumber());
//                        schedule.setMaxCapacity(template.getMaxCapacity());
//                        schedule.setCreateAt(LocalDate.now());
//                        schedule.setStatus(ScheduleStatus.WORKING);
//                        schedule.setAssignTo(template.getAccount());
//                        schedule.setAccount(account);
//                        schedule.setShift(template.getShift());
//                        scheduleRepository.save(schedule);
//                    }
//                }
//                scheduleStartDate = scheduleStartDate.plusDays(1);
//            }
//        }
//    }
//
//    public void generateSchedulesForTemplate(Long templateId,
//                                             LocalDate startDate,
//                                             LocalDate endDate,
//                                             Authentication authentication) throws NotFoundException {
//        Account account = accountRepository.findByEmail(authentication.getName());
//        ScheduleTemplate template = scheduleTemplateRepository.findById(templateId)
//                .orElseThrow(() -> new NotFoundException("Template not found"));
//
//        LocalDate currentDate = startDate;
//        while (!currentDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
//            currentDate = currentDate.plusDays(1);
//        }
//        while (!currentDate.isAfter(endDate)) {
//            if (!scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
//                    template.getAccount().getId(), template.getShift().getId().intValue(), currentDate)) {
//                Schedule schedule = new Schedule();
//                schedule.setWorkDate(currentDate);
//                schedule.setRoomNumber(template.getRoomNumber());
//                schedule.setMaxCapacity(template.getMaxCapacity());
//                schedule.setCreateAt(LocalDate.now());
//                schedule.setStatus(ScheduleStatus.WORKING);
//                schedule.setAssignTo(template.getAccount());
//                schedule.setAccount(account);
//                schedule.setShift(template.getShift());
//                scheduleRepository.save(schedule);
//            }
//            currentDate = currentDate.plusDays(7);
//        }
//    }

    public List<LocalTime> getAvailableSlots(Long id, LocalDate workDate) {
        Account doctor;
        Optional<Account> doctorOpt =  accountRepository.findById(id);
        if (doctorOpt.isPresent()) {
            doctor = doctorOpt.get();
        } else {
            throw new IllegalArgumentException("Doctor not found");
        }
        List<LocalTime> availableSlots = new ArrayList<>();
        List<Schedule>  schedules = scheduleRepository.findByAssignToIdAndWorkDate(id, workDate);
        for (Schedule schedule : schedules) {
            List<LocalTime> allSlots = shiftService.generateSlot(schedule.getShift().getStartTime(), schedule.getShift().getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments) {
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots) {
                if(!booked.contains(slotTime)){
                    availableSlots.add(slotTime);
                }
            }
        }
        Collections.sort(availableSlots);
        return availableSlots;
    }

    public Schedule findSchedule(Long id, LocalDate workDate, int shiftId) {
        Schedule schedule = scheduleRepository.findByAssignToIdAndWorkDateAndShiftId(id, workDate, shiftId);
        if(schedule == null){
            throw new IllegalArgumentException("Schedule not found");
        }
        return schedule;
    }

    public List<LocalDate> getAvailableSchedulesByDoctor(@Min(1) Long id) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth());
        Account doctor = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(doctor.getId(), startDate, endDate);
        List<LocalDate> availableSchedules = new ArrayList<>();
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(doctor.getId(), schedule.getWorkDate());
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            boolean hasFreeSlots = allSlots.stream().anyMatch(slot -> !booked.contains(slot));
            if(hasFreeSlots){
                availableSchedules.add(schedule.getWorkDate());
            }
        }
        Collections.sort(availableSchedules);
        return availableSchedules;
    }


    public List<LocalDate> getAvailableDatesByDate() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth());
        List<LocalDate> availableDates = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByWorkDateBetween(startDate, endDate);
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndTime(schedule.getAssignTo().getId(), schedule.getWorkDate());
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime) && !availableDates.contains(schedule.getWorkDate())){
                    availableDates.add(schedule.getWorkDate());
                }
            }
        }
        Collections.sort(availableDates);
        return availableDates;
    }

    public List<LocalTime>  getAvailableSlotsByDate(LocalDate date) {
        List<LocalTime> availableSlots = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByWorkDate(date);
        if(schedules.isEmpty()){
            throw new RuntimeException("Schedule not found");
        }
        for(Schedule schedule : schedules){
            Shift shift = schedule.getShift();
            List<Appointment>  appointments = appointmentRepository.findByDoctorIdAndTime(schedule.getAssignTo().getId(), schedule.getWorkDate());
            List<LocalTime> allSlots = shiftService.generateSlot(shift.getStartTime(), shift.getEndTime(), Duration.ofMinutes(30));
            Set<LocalTime> booked = new HashSet<>();
            for(Appointment appointment : appointments){
                if(appointment.getStartTime() != null){
                    booked.add(appointment.getStartTime());
                }
            }
            for(LocalTime slotTime : allSlots){
                if(!booked.contains(slotTime) && !availableSlots.contains(slotTime)){
                    availableSlots.add(slotTime);
                }
            }
        }
        return availableSlots;
    }

    public void createSchedule(@Valid ScheduleRequest request,
                               Authentication authentication) {
        Account assignBy = accountRepository.findByEmail(authentication.getName());
        List<ScheduleTemplate> templates = templateRepository.findAll();
        Role doctorRole = roleRepository.findByRoleName(AccountRole.ROLE_DOCTOR)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Role staffRole = roleRepository.findByRoleName(AccountRole.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<Role> doctorRoles = List.of(doctorRole);
        List<Role> staffRoles = List.of(staffRole);
        List<Account> doctors = accountRepository.findByRolesAndDoctorStatus(doctorRoles, EmploymentStatus.ACTIVE);
        List<Account> staffs = accountRepository.findByRolesAndStaffStatus(staffRoles, EmploymentStatus.ACTIVE);
        LocalDate currentDate = request.getStartDate();
        int doctorIndex = 0;
        int staffIndex = 0;
        while(currentDate.isBefore(request.getEndDate())){
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
            List<ScheduleTemplate> templatesToday = new ArrayList<>();
            for(ScheduleTemplate template : templates){
                if(template.getDayOfWeek().name().equals(currentDayOfWeek.name())){
                    templatesToday.add(template);
                }
            }
            if(templatesToday.isEmpty() || templatesToday == null){
                currentDate = currentDate.plusDays(1);
            } else {
                Set<Account> selectedDoctorsToday = new HashSet<>();

                for(int i=0; i<templatesToday.get(0).getMaxDoctors(); i++){
                    Account doctor = doctors.get(doctorIndex);
                    selectedDoctorsToday.add(doctor);
                    doctorIndex = (doctorIndex+1) % doctors.size();
                }

                Set<Account> selectedStaffsToday = new HashSet<>();
                for(int i=0; i<templatesToday.get(0).getMaxStaffs(); i++){
                    Account staff = staffs.get(staffIndex);
                    selectedStaffsToday.add(staff);
                    staffIndex = (staffIndex+1) % staffs.size();
                }

                for(ScheduleTemplate template : templatesToday){
                    Shift shift = template.getShift();

                    for(Account doctor : selectedDoctorsToday){
                        boolean exists = scheduleRepository.existsByAssignToAndShiftAndWorkDate(doctor, shift, currentDate);
                        if(exists) continue;
                        Schedule schedule = new Schedule();
                        schedule.setWorkDate(currentDate);
                        schedule.setCreateAt(LocalDate.now());
                        schedule.setStatus(ScheduleStatus.WORKING);
                        schedule.setAccount(assignBy);
                        schedule.setAppointments(null);
                        schedule.setAssignTo(doctor);
                        schedule.setShift(shift);
                        schedule.setReplace(null);
                        scheduleRepository.save(schedule);
                    }

                    for(Account staff : selectedStaffsToday){
                        boolean exists = scheduleRepository.existsByAssignToAndShiftAndWorkDate(staff, shift, currentDate);
                        if(exists) continue;
                        Schedule schedule = new Schedule();
                        schedule.setWorkDate(currentDate);
                        schedule.setCreateAt(LocalDate.now());
                        schedule.setStatus(ScheduleStatus.WORKING);
                        schedule.setAccount(assignBy);
                        schedule.setAppointments(null);
                        schedule.setAssignTo(staff);
                        schedule.setShift(shift);
                        schedule.setReplace(null);
                        scheduleRepository.save(schedule);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }

        }
    }

    public Map<LocalDate, SuggestionResponse> getSuggestionList(@Valid @Min(1) Long id,
                                                                @Valid @NotNull LocalDate fromDate,
                                                                @Valid @NotNull LocalDate toDate) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        Account replace = application.getDoctor();
        Role role = application.getDoctor().getRoles().iterator().next();
        List<Account> hasRole = accountRepository.findByRoles(List.of(role));
        List<Schedule> schedulesInRange = scheduleRepository.findByAssignToIdAndWorkDateBetween(replace.getId(),  fromDate, toDate);
        Set<LocalDate> workDates = new HashSet<>();
        for(Schedule schedule : schedulesInRange){
            if(schedule.getAssignTo().getId().equals(replace.getId())){
                workDates.add(schedule.getWorkDate());
            }
        }
        List<Schedule> allSchedulesInRange = scheduleRepository.findByWorkDateBetween(fromDate, toDate);
        Map<LocalDate, Set<Long>> assignedByDate = new HashMap<>();
        for(Schedule schedule : allSchedulesInRange){
            LocalDate workDate = schedule.getWorkDate();
            Long assignedId = schedule.getAssignTo().getId();
            if(!assignedByDate.containsKey(workDate)){
                assignedByDate.put(workDate, new HashSet<>());
            }
            assignedByDate.get(workDate).add(assignedId);
        }

        Map<LocalDate, SuggestionResponse> suggestionsByDate = new HashMap<>();
        for(LocalDate date : workDates){
            Set<Long> assignedIds = assignedByDate.getOrDefault(date, Collections.emptySet());

            List<AccountResponse> suggestions = new ArrayList<>();
            for(Account account : hasRole){
                if(!assignedIds.contains(account.getId())){
                    suggestions.add(new AccountResponse(account));
                }
            }
            SuggestionResponse suggestionResponse = new SuggestionResponse(replace.getId(),  suggestions);
            suggestionsByDate.put(date, suggestionResponse);
        }
        return suggestionsByDate;
    }

    public boolean setReplaceEmployment(@Valid @Min(1) Long replacedId,
                                                 @Valid @Min(1) Long suggestId,
                                                 @Valid @NotNull LocalDate workDate) {
        boolean check = false;
        Account replaceAccount = accountRepository.findById(suggestId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDate(replacedId, workDate);
        for(Schedule schedule : schedules){
            schedule.setReplace(replaceAccount);
            scheduleRepository.save(schedule);
            check = true;
        }
        return check;
    }

    public List<ScheduleResponse> getMineSchedule(Authentication authentication,
                                                  @Valid @NotNull LocalDate fromDate,
                                                  @Valid @NotNull LocalDate toDate) {
        Account account = accountRepository.findByEmail(authentication.getName());
        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(account.getId(), fromDate, toDate);
        List<ScheduleResponse> responses = new ArrayList<>();
        for(Schedule schedule : schedules){
            responses.add(new ScheduleResponse(schedule));
        }
        return responses;
    }
}

