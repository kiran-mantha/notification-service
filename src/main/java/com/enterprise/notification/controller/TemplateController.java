package com.enterprise.notification.controller;

import com.enterprise.notification.domain.entity.Template;
import com.enterprise.notification.dto.TemplateRequest;
import com.enterprise.notification.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
@Tag(name = "Templates", description = "Template management APIs")
public class TemplateController {
    
    private final TemplateService templateService;
    
    @PostMapping
    @Operation(summary = "Create a new template")
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody TemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(templateService.createTemplate(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a template")
    public ResponseEntity<Template> updateTemplate(
        @PathVariable Long id,
        @Valid @RequestBody TemplateRequest request
    ) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<Template> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplate(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all templates")
    public ResponseEntity<Page<Template>> getAllTemplates(Pageable pageable) {
        return ResponseEntity.ok(templateService.getAllTemplates(pageable));
    }
}
