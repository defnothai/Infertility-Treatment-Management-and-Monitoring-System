package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.Notification;
import com.fuhcm.swp391.be.itmms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuthenticationService authenticationService;

    public Notification createNotification(Account user, String content) {
        Notification n = new Notification();
        n.setAccount(user);
        n.setContent(content);
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepo.save(n);
    }

    public void notifyUser(Account user, String content) {
        Notification n = createNotification(user, content);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/notifications",
                content
        );
    }

    public List<Notification> getAllNotifications() {
        Account account = authenticationService.getCurrentAccount();
        return notificationRepo.findByAccountOrderByCreatedAtDesc(account);
    }

    public void markAllAsRead() {
        Account user = authenticationService.getCurrentAccount();
        List<Notification> unreadNotifications = notificationRepo.findByAccountAndIsReadFalse(user);
        for (Notification n : unreadNotifications) {
            n.setRead(true);
        }
        notificationRepo.saveAll(unreadNotifications);
    }
}
