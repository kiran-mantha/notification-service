package com.enterprise.notification.dto;

import lombok.Data;

@Data
public class UserPreferenceRequest {
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean inAppEnabled;
    private Boolean webhookEnabled;
}
