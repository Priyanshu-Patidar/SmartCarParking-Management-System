package com.smartparking.service;

import com.smartparking.entity.AuditLog;
import com.smartparking.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String userEmail, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .userEmail(userEmail)
                .action(action)
                .details(details)
                .build());
    }

    public Page<AuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
