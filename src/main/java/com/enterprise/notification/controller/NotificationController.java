package com.enterprise.notification.controller;

import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.dto.BulkNotificationRequest;
import com.enterprise.notification.dto.NotificationRequest;
import com.enterprise.notification.dto.NotificationResponse;
import com.enterprise.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/send")
    @Operation(summary = "Send a notification")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(notificationService.sendNotification(request));
    }
    
    @PostMapping("/bulk-send")
    @Operation(summary = "Send bulk notifications")
    public ResponseEntity<List<NotificationResponse>> sendBulkNotifications(
        @Valid @RequestBody BulkNotificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(notificationService.sendBulkNotifications(request.getNotifications()));
    }
    
    @PostMapping("/schedule")
    @Operation(summary = "Schedule a notification")
    public ResponseEntity<NotificationResponse> scheduleNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(notificationService.sendNotification(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.getNotification(id));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUser(
        @PathVariable String userId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId, pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search notifications with filters")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) NotificationStatus status,
        @RequestParam(required = false) NotificationType type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            notificationService.searchNotifications(userId, status, type, startDate, endDate, pageable)
        );
    }
}
