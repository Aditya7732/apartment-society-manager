package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.audit.AuditLogDto;
import com.society.manager.entity.AuditLog;
import com.society.manager.entity.User;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.AuditLogRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public void logAction(UUID userId, String action, String entityType, String entityId, String ipAddress, String previousValue, String newValue) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .ipAddress(ipAddress)
                .previousValue(previousValue)
                .newValue(newValue)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit Logged: action={}, entityType={}, entityId={}, user={}", action, entityType, entityId, (user != null ? user.getUsername() : "System"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogDto> getAllLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(entityMapper::toAuditLogDto));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogDto> getLogsByUser(UUID userId, Pageable pageable) {
        // Simple specification or method mapping
        Page<AuditLog> page = auditLogRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("user").get("id"), userId),
                pageable
        );
        return PageResponse.fromPage(page.map(entityMapper::toAuditLogDto));
    }
}
