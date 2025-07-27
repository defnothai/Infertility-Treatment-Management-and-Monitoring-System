package com.fuhcm.swp391.be.itmms.components;

import com.fuhcm.swp391.be.itmms.constant.TreatmentPlanStatus;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentPlan;
import com.fuhcm.swp391.be.itmms.entity.treatment.TreatmentSession;
import com.fuhcm.swp391.be.itmms.repository.TreatmentPlanRepository;
import com.fuhcm.swp391.be.itmms.repository.TreatmentSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TreatmentPlanScheduler {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentSessionRepository treatmentSessionRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void autoMarkDroppedTreatmentPlans() {
        List<TreatmentPlan> activePlans = treatmentPlanRepository.findByStatus(TreatmentPlanStatus.IN_PROGRESS);
        LocalDateTime now = LocalDateTime.now();

        for (TreatmentPlan plan : activePlans) {
            TreatmentSession lastSession = treatmentSessionRepository
                    .findTopByProgress_Plan_IdOrderByDateDesc(plan.getId());

            if (lastSession != null) {
                Duration daysSinceLast = Duration.between(lastSession.getDate(), now);
                if (daysSinceLast.toDays() > 30) {
                    plan.setStatus(TreatmentPlanStatus.CANCELLED);
                    plan.setNotes("Không tiếp tục sau " + daysSinceLast.toDays() + " ngày.");
                    treatmentPlanRepository.save(plan);
                }
            }
        }
    }


}
