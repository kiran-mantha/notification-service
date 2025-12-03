package com.enterprise.notification.controller;

import com.enterprise.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin APIs for notification management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/retry/{notificationId}")
    @Operation(summary = "Retry a failed notification")
    public ResponseEntity<Void> retryNotification(@PathVariable UUID notificationId) {
        notificationService.retryNotification(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/cancel/{notificationId}")
    @Operation(summary = "Cancel a notification")
    public ResponseEntity<Void> cancelNotification(@PathVariable UUID notificationId) {
        notificationService.cancelNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
