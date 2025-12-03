package com.enterprise.notification.service;

import com.enterprise.notification.domain.entity.Template;
import com.enterprise.notification.dto.TemplateRequest;
import com.enterprise.notification.repository.TemplateRepository;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateService {
    
    private final TemplateRepository templateRepository;
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    
    @Transactional
    public Template createTemplate(TemplateRequest request) {
        Template template = Template.builder()
            .name(request.getName())
            .channel(request.getChannel())
            .subject(request.getSubject())
            .body(request.getBody())
            .placeholders(request.getPlaceholders())
            .build();
        
        return templateRepository.save(template);
    }
    
    @Transactional
    public Template updateTemplate(Long id, TemplateRequest request) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        
        template.setName(request.getName());
        template.setChannel(request.getChannel());
        template.setSubject(request.getSubject());
        template.setBody(request.getBody());
        template.setPlaceholders(request.getPlaceholders());
        
        return templateRepository.save(template);
    }
    
    public Template getTemplate(Long id) {
        return templateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));
    }
    
    public Page<Template> getAllTemplates(Pageable pageable) {
        return templateRepository.findAll(pageable);
    }
    
    public String renderTemplate(Long templateId, Map<String, Object> data) {
        Template template = getTemplate(templateId);
        return renderTemplate(template.getBody(), data);
    }
    
    public String renderTemplate(String templateContent, Map<String, Object> data) {
        Mustache mustache = mustacheFactory.compile(new StringReader(templateContent), "template");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, data);
        return writer.toString();
    }
}
