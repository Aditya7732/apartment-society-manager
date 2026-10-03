package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.audit.AuditLogDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditLogService {
    void logAction(UUID userId, String action, String entityType, String entityId, String ipAddress, String previousValue, String newValue);
    PageResponse<AuditLogDto> getAllLogs(Pageable pageable);
    PageResponse<AuditLogDto> getLogsByUser(UUID userId, Pageable pageable);
}
