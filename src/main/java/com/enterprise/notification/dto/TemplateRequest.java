package com.enterprise.notification.dto;

import com.enterprise.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TemplateRequest {
    
    @NotBlank(message = "Template name is required")
    private String name;
    
    @NotNull(message = "Channel is required")
    private NotificationType channel;
    
    private String subject;
    
    @NotBlank(message = "Body is required")
    private String body;
    
    private List<String> placeholders;
}
