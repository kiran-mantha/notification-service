package com.enterprise.notification.service;

import com.enterprise.notification.domain.entity.UserPreference;
import com.enterprise.notification.dto.UserPreferenceRequest;
import com.enterprise.notification.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {
    
    private final UserPreferenceRepository userPreferenceRepository;
    
    public UserPreference getUserPreference(String userId) {
        return userPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreference(userId));
    }
    
    @Transactional
    public UserPreference updateUserPreference(String userId, UserPreferenceRequest request) {
        UserPreference preference = getUserPreference(userId);
        
        if (request.getEmailEnabled() != null) {
            preference.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getSmsEnabled() != null) {
            preference.setSmsEnabled(request.getSmsEnabled());
        }
        if (request.getPushEnabled() != null) {
            preference.setPushEnabled(request.getPushEnabled());
        }
        if (request.getInAppEnabled() != null) {
            preference.setInAppEnabled(request.getInAppEnabled());
        }
        if (request.getWebhookEnabled() != null) {
            preference.setWebhookEnabled(request.getWebhookEnabled());
        }
        
        return userPreferenceRepository.save(preference);
    }
    
    private UserPreference createDefaultPreference(String userId) {
        UserPreference preference = UserPreference.builder()
            .userId(userId)
            .emailEnabled(true)
            .smsEnabled(true)
            .pushEnabled(true)
            .inAppEnabled(true)
            .webhookEnabled(true)
            .build();
        
        return userPreferenceRepository.save(preference);
    }
}
