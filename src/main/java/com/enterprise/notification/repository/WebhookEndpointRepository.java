package com.enterprise.notification.repository;

import com.enterprise.notification.domain.entity.WebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    List<WebhookEndpoint> findByUserIdAndIsActive(String userId, Boolean isActive);
}
