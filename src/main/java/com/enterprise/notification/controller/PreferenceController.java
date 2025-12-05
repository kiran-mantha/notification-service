package com.enterprise.notification.controller;

import com.enterprise.notification.domain.entity.UserPreference;
import com.enterprise.notification.dto.UserPreferenceRequest;
import com.enterprise.notification.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
@Tag(name = "Preferences", description = "User preference management APIs")
public class PreferenceController {
    
    private final UserPreferenceService userPreferenceService;
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user preferences")
    public ResponseEntity<UserPreference> getUserPreference(@PathVariable String userId) {
        return ResponseEntity.ok(userPreferenceService.getUserPreference(userId));
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user preferences")
    public ResponseEntity<UserPreference> updateUserPreference(
        @PathVariable String userId,
        @RequestBody UserPreferenceRequest request
    ) {
        return ResponseEntity.ok(userPreferenceService.updateUserPreference(userId, request));
    }
}
