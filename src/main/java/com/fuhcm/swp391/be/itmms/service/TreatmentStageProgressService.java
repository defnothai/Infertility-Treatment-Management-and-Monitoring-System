package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentSessionStatus;
import com.fuhcm.swp391.be.itmms.constant.TreatmentStageStatus;
import com.fuhcm.swp391.be.itmms.dto.request.TreatmentStageProgressRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ProgressStatusResponse;
import com.fuhcm.swp391.be.itmms.dto.response.TreatmentStageProgressResponse;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentStageProgress;
import com.fuhcm.swp391.be.itmms.repository.TreatmentPlanRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentStageProgressRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentStageProgressService {

    private final TreatmentStageProgressRepository treatmentStageProgressRepository;
    private final ModelMapper modelMapper;
    private final MedicalRecordAccessService medicalRecordAccessService;
    private final TreatmentPlanRepository treatmentPlanRepository;

    // hoàn tất giai đoạn
    public TreatmentStageProgressResponse update(Long id, TreatmentStageProgressRequest request) throws NotFoundException {
        TreatmentStageProgress progress = treatmentStageProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giai đoạn của phác đồ không tồn tại"));
        TreatmentPlan existPlan = progress.getPlan();
        if (existPlan.getStatus() == TreatmentPlanStatus.COMPLETED || existPlan.getStatus() == TreatmentPlanStatus.CANCELLED) {
            throw new RuntimeException("Không thể cập nhật khi phác đồ đã hủy hoặc đã hoàn thành");
        }
        // còn session chưa hoàn thành thì không cho hoàn tất
        boolean hasPendingSession = progress.getSessions()
                .stream()
                .anyMatch(treatmentSession
                        -> treatmentSession.getStatus() == TreatmentSessionStatus.PENDING);
        if (hasPendingSession) {
            throw new RuntimeException("Tồn tại buổi khám chưa hoàn thành, không thể hoàn tất giai đoạn điều trị");
        }
        // kiểm tra quyền truy cập hồ sơ
        MedicalRecord medicalRecord = progress.getPlan().getMedicalRecord();
        if (medicalRecord != null && !medicalRecordAccessService.canUpdate(medicalRecord)) {
            throw new AccessDeniedException("Bạn không thể sử dụng tính năng này");
        }
        //
        progress.setDayComplete(LocalDate.now());
        progress.setNotes(request.getNotes());
        progress.setStatus(TreatmentStageStatus.COMPLETED);
        TreatmentStageProgress saved = treatmentStageProgressRepository.save(progress);

        // update plan status nếu là stage cuối dùng
        if (isLastTreatmentStage(saved)) {
            TreatmentPlan plan = saved.getPlan();
            plan.setDayEnd(LocalDate.now());
            plan.setStatus(TreatmentPlanStatus.COMPLETED);
            plan.setNotes("Bệnh nhân hoàn thành phác đồ điều trị");
            treatmentPlanRepository.save(plan);
        }
        // response
        TreatmentStageProgressResponse response = modelMapper.map(saved, TreatmentStageProgressResponse.class);
        if (saved.getServiceStage() != null) {
            response.setStageName(saved.getServiceStage().getName());
        }
        return response;
    }

    // lấy status của stage để fe hiển thị thay đổi status
    public ProgressStatusResponse getProgressStatus(Long id) throws NotFoundException {
        TreatmentStageProgress progress = treatmentStageProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giai đoạn của phác đồ không tồn tại"));
        return new ProgressStatusResponse(progress.getId(), progress.getStatus());
    }

    // Lấy stage cuối cùng của plan
    public boolean isLastTreatmentStage(TreatmentStageProgress currentStageProgress) {
        com.fuhcm.swp391.be.itmms.entity.service.Service service = currentStageProgress.getPlan().getService();

        int maxStageOrder = service.getStage().stream()
                .mapToInt(ServiceStage::getStageOrder)
                .max()
                .orElseThrow(() -> new IllegalStateException("Dịch vụ chưa có các giai đoạn cụ thể"));

        int currentStageOrder = currentStageProgress.getServiceStage().getStageOrder();
        return currentStageOrder == maxStageOrder;
    }


}

