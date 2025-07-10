package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.HospitalAchievementRequest;
import com.fuhcm.swp391.be.itmms.dto.response.HospitalAchievementResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.HospitalAchievement;
import com.fuhcm.swp391.be.itmms.repository.HospitalAchievementRepository;
import com.fuhcm.swp391.be.itmms.utils.SlugUtil;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalAchievementService {

    private final HospitalAchievementRepository repository;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    public List<HospitalAchievementResponse> getActiveAchievements() {
        return repository.findByIsActiveTrue()
                .stream()
                .map(achievement -> {
                    HospitalAchievementResponse response = modelMapper.map(achievement, HospitalAchievementResponse.class);
                    response.setCreatedByName(achievement.getCreatedBy().getFullName());
                    return response;
                })
                .toList();
    }

    public HospitalAchievementResponse createAchievement(HospitalAchievementRequest request) {
        Account creator = authenticationService.getCurrentAccount();

        HospitalAchievement achievement = modelMapper.map(request, HospitalAchievement.class);
        achievement.setActive(true);
        achievement.setCreatedBy(creator);
        achievement.setSlug(SlugUtil.toSlug(achievement.getTitle()));
        HospitalAchievement saved = repository.save(achievement);

        HospitalAchievementResponse response = modelMapper.map(saved, HospitalAchievementResponse.class);
        response.setCreatedByName(saved.getCreatedBy().getFullName());
        return response;
    }

    public HospitalAchievementResponse updateAchievement(Long id, HospitalAchievementRequest request) throws NotFoundException, AccessDeniedException {
        HospitalAchievement achievement = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành tích bệnh viện"));
        if (authenticationService.getCurrentAccount().getId() != achievement.getCreatedBy().getId()) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật thành tích này");
        }
        modelMapper.map(request, achievement);
        achievement.setSlug(SlugUtil.toSlug(achievement.getTitle()));
        HospitalAchievement updated = repository.save(achievement);
        HospitalAchievementResponse response = modelMapper.map(updated, HospitalAchievementResponse.class);
        response.setCreatedByName(updated.getCreatedBy().getFullName());
        return response;
    }

    public void deleteAchievement(Long id) throws NotFoundException {
        HospitalAchievement achievement = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành tích"));

        if (authenticationService.getCurrentAccount().getId() != achievement.getCreatedBy().getId()) {
            throw new AccessDeniedException("Bạn không có quyền xóa thành tích này");
        }

        achievement.setActive(false);
        repository.save(achievement);
    }

    public HospitalAchievementResponse getAchievementById(Long id) throws NotFoundException {
        HospitalAchievement achievement = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành tích"));

        if (!achievement.isActive()) throw new NotFoundException("Bài viết đã bị xóa hoặc ẩn đi");

        HospitalAchievementResponse response = modelMapper.map(achievement, HospitalAchievementResponse.class);
        response.setCreatedByName(achievement.getCreatedBy().getFullName());
        return response;
    }



}
