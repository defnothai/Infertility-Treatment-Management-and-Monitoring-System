package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ScheduleTemplateResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import com.fuhcm.swp391.be.itmms.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        boolean exists = scheduleTemplateRepository.existsByAccountIdAndShiftIdAndDayOfWeek(
                request.getAccountId(), request.getShiftId().intValue(), request.getDayOfWeek());
        if (exists) {
            throw new RuntimeException("Schedule template already exists");
        }

        ScheduleTemplate newTemplate = new ScheduleTemplate();
        newTemplate.setDayOfWeek(request.getDayOfWeek());
        newTemplate.setRoomNumber(request.getRoomNumber());
        newTemplate.setMaxCapacity(0);

        Shift shift = shiftRepository.findById(request.getShiftId().intValue())
                .orElseThrow(() -> new RuntimeException("Shift with id " + request.getShiftId() + " not found"));
        newTemplate.setShift(shift);

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account with id " + request.getAccountId() + " not found"));
        newTemplate.setAccount(account);

        ScheduleTemplate saved = scheduleTemplateRepository.save(newTemplate);

        ScheduleTemplateResponse dto = new ScheduleTemplateResponse();
        dto.setId(saved.getId());
        dto.setDayOfWeek(saved.getDayOfWeek());
        dto.setRoomNumber(saved.getRoomNumber());
        dto.setMaxCapacity(saved.getMaxCapacity());
        dto.setAccountId(account.getId());
        dto.setAccountFullName(account.getFullName());
        dto.setShiftId(shift.getId());
        dto.setShiftTime(shift.getStartTime() + " - " + shift.getEndTime());
        return dto;
    }

}
