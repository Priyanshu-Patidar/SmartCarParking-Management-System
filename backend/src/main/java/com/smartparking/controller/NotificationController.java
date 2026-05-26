package com.smartparking.controller;

import com.smartparking.entity.Notification;
import com.smartparking.service.NotificationService;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id, SecurityUtils.getCurrentUser().getId());
        return ResponseEntity.ok().build();
    }
}
