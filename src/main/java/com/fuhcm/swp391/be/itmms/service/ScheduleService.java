package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ScheduleStatus;
import com.fuhcm.swp391.be.itmms.dto.request.WeeklyScheduleRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Schedule;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<ScheduleResponse> getSchedulesByWeekYear(WeeklyScheduleRequest request, Authentication authentication) {
        Account currentUser = accountRepository.findByEmail(authentication.getName());

        String[] parts = request.getWeekly().split(" to ");
        String[] startParts = parts[0].split("/");
        String[] endParts = parts[1].split("/");

        int year = request.getYear();
        int startDay = Integer.parseInt(startParts[0]);
        int startMonth = Integer.parseInt(startParts[1]);
        int endDay = Integer.parseInt(endParts[0]);
        int endMonth = Integer.parseInt(endParts[1]);

        LocalDate startDate = LocalDate.of(year, startMonth, startDay);
        LocalDate endDate = LocalDate.of(year, endMonth, endDay);

        List<Schedule> schedules = scheduleRepository.findByAssignToIdAndWorkDateBetween(
                currentUser.getId(), startDate, endDate);
        return schedules.stream().map(schedule -> {
            ScheduleResponse dto = modelMapper.map(schedule, ScheduleResponse.class);
            dto.setShiftTime(schedule.getShift().getStartTime() + " - " + schedule.getShift().getEndTime());
            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public void generateStaffSchedules(LocalDate startDate,
                                       LocalDate endDate,
                                       Authentication authentication) throws BadRequestException {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Thời gian nhập không hợp lệ");
        }
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findAll();
        Account account = accountRepository.findByEmail(authentication.getName());

        for (ScheduleTemplate template : templates) {
            LocalDate scheduleStartDate = startDate;
            while (!scheduleStartDate.isAfter(endDate)) {
                if (scheduleStartDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
                    boolean exists = scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
                            template.getAccount().getId(), template.getShift().getId().intValue(), scheduleStartDate);
                    if (!exists) {
                        Schedule schedule = new Schedule();
                        schedule.setWorkDate(scheduleStartDate);
                        schedule.setRoomNumber(template.getRoomNumber());
                        schedule.setMaxCapacity(template.getMaxCapacity());
                        schedule.setCreateAt(LocalDate.now());
                        schedule.setStatus(ScheduleStatus.WORKING);
                        schedule.setAssignTo(template.getAccount());
                        schedule.setAccount(account);
                        schedule.setShift(template.getShift());
                        scheduleRepository.save(schedule);
                    }
                }
                scheduleStartDate = scheduleStartDate.plusDays(1);
            }
        }
    }

    public void generateSchedulesForTemplate(Long templateId,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             Authentication authentication) throws NotFoundException {
        Account account = accountRepository.findByEmail(authentication.getName());
        ScheduleTemplate template = scheduleTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Template not found"));

        LocalDate currentDate = startDate;
        while (!currentDate.getDayOfWeek().name().equals(template.getDayOfWeek().name())) {
            currentDate = currentDate.plusDays(1);
        }
        while (!currentDate.isAfter(endDate)) {
            if (!scheduleRepository.existsByAssignToIdAndShiftIdAndWorkDate(
                    template.getAccount().getId(), template.getShift().getId().intValue(), currentDate)) {
                Schedule schedule = new Schedule();
                schedule.setWorkDate(currentDate);
                schedule.setRoomNumber(template.getRoomNumber());
                schedule.setMaxCapacity(template.getMaxCapacity());
                schedule.setCreateAt(LocalDate.now());
                schedule.setStatus(ScheduleStatus.WORKING);
                schedule.setAssignTo(template.getAccount());
                schedule.setAccount(account);
                schedule.setShift(template.getShift());
                scheduleRepository.save(schedule);
            }
            currentDate = currentDate.plusDays(7);
        }
    }
}
