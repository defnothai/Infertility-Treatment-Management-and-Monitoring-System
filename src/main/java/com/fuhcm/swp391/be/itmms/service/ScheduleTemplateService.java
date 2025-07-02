package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.ScheduleTemplateRequest;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.ScheduleTemplate;
import com.fuhcm.swp391.be.itmms.entity.Shift;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.ScheduleTemplateRepository;
import com.fuhcm.swp391.be.itmms.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScheduleTemplateService {

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ScheduleTemplate createTemplate(ScheduleTemplateRequest scheduleTemplate) {
        boolean exists = scheduleTemplateRepository.existsByAccountIdAndShiftIdAndDayOfWeek(scheduleTemplate.getAccount().getId(), scheduleTemplate.getShift().getId().intValue(), scheduleTemplate.getDayOfWeek());
        if(exists){
            throw new RuntimeException("Schedule template already exists");
        }
        ScheduleTemplate newTemplate = new ScheduleTemplate();
        newTemplate.setDayOfWeek(scheduleTemplate.getDayOfWeek());
        newTemplate.setRoomNumber(scheduleTemplate.getRoomNumber());
        newTemplate.setMaxCapacity(scheduleTemplate.getMaxCapacity());
        Optional<Shift> shiftOpt = shiftRepository.findById(scheduleTemplate.getShift().getId().intValue());
        if (shiftOpt.isPresent()) {
            Shift shift = shiftOpt.get();
            newTemplate.setShift(shift);
        } else {
            throw new RuntimeException("Shift with id " + scheduleTemplate.getShift().getId() + " not found");
        }
        Optional<Account> accountOpt = accountRepository.findById(scheduleTemplate.getAccount().getId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            newTemplate.setAccount(account);
        } else {
            throw new RuntimeException("Account with id " + scheduleTemplate.getAccount().getId() + " not found");
        }
        return scheduleTemplateRepository.save(newTemplate);
    }
}
