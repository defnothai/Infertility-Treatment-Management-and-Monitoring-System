package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.Notification;
import com.fuhcm.swp391.be.itmms.service.NotificationService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity getAllNotifications() throws NotFoundException {
        List<Notification> notifications = notificationService.getAllNotifications();
        if (notifications.isEmpty()) {
            throw new NotFoundException("Không có thông báo nào");
        }
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "FETCH_SUCCESS",
                        "Lấy dữ liệu thành công",
                        notifications)
        );
    }

    @PutMapping("/mark-read")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(
                new ResponseFormat<>(HttpStatus.OK.value(),
                        "UPDATE_SUCCESS",
                        "Cập nhật thành công",
                        null)
        );
    }


}
