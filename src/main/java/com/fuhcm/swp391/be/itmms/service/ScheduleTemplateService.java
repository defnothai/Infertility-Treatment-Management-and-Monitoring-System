package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleTemplateResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import com.fuhcm.swp391.be.itmms.repository.ShiftRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleTemplateService {

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public ScheduleTemplateResponse createTemplateStaff(ScheduleTemplateRequest request) {
//        boolean exists = scheduleTemplateRepository.existsByAccountIdAndShiftIdAndDayOfWeek(
//                request.getAccountId(), request.getShiftId().intValue(), request.getDayOfWeek());
//        if (exists) {
//            throw new RuntimeException("Schedule template already exists");
//        }
        Shift shift = shiftRepository.findById(request.getShiftId()).get();
        boolean exists = scheduleTemplateRepository.existsByDayOfWeekAndShift(request.getDayOfWeek(), shift);
        if(exists) throw new RuntimeException("Schedule template already exists");

        ScheduleTemplate newTemplate = new ScheduleTemplate();
        newTemplate.setDayOfWeek(request.getDayOfWeek());
//        newTemplate.setRoomNumber(request.getRoomNumber());
        newTemplate.setMaxStaffs(request.getMaxStaffs());
        newTemplate.setMaxDoctors(request.getMaxDoctors());
        newTemplate.setShift(shift);
//        newTemplate.setMaxCapacity(0);

//        Shift shift = shiftRepository.findById(request.getShiftId().intValue())
//                .orElseThrow(() -> new RuntimeException("Shift with id " + request.getShiftId() + " not found"));
//        newTemplate.setShift(shift);

//        Account account = accountRepository.findById(request.getAccountId())
//                .orElseThrow(() -> new RuntimeException("Account with id " + request.getAccountId() + " not found"));
//        newTemplate.setAccount(account);

//        ScheduleTemplate saved =
        scheduleTemplateRepository.save(newTemplate);

//        ScheduleTemplateResponse dto = new ScheduleTemplateResponse(newTemplate);
//        dto.setId(saved.getId());
//        dto.setDayOfWeek(saved.getDayOfWeek());
//        dto.setRoomNumber(saved.getRoomNumber());
//        dto.setMaxCapacity(saved.getMaxCapacity());
//        dto.setAccountId(account.getId());
//        dto.setAccountFullName(account.getFullName());
//        dto.setShiftId(shift.getId());
//        dto.setShiftTime(shift.getStartTime() + " - " + shift.getEndTime());
        return new ScheduleTemplateResponse(newTemplate);
    }

    public ScheduleTemplateResponse updateScheduleTemplate(@Valid ScheduleTemplateRequest request,
                                                           @Valid @Min(1) Long id) {
        ScheduleTemplate  scheduleTemplate = scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule template not found"));
        scheduleTemplate.setDayOfWeek(request.getDayOfWeek());
        scheduleTemplate.setMaxStaffs(request.getMaxStaffs());
        scheduleTemplate.setMaxDoctors(request.getMaxDoctors());
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        scheduleTemplate.setShift(shift);
        scheduleTemplateRepository.save(scheduleTemplate);
        return new ScheduleTemplateResponse(scheduleTemplate);
    }

    public List<ScheduleTemplateResponse> getAllScheduleTemplate() {
        List<ScheduleTemplateResponse> responses = new ArrayList<>();
        List<ScheduleTemplate>  scheduleTemplates = scheduleTemplateRepository.findAll();
        for(ScheduleTemplate scheduleTemplate : scheduleTemplates){
            responses.add(new ScheduleTemplateResponse(scheduleTemplate));
        }
        return responses;
    }
}
