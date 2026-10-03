package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.audit.AuditLogDto;
import com.society.manager.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
@Tag(name = "Audit Logs", description = "Endpoints for inspecting system security and operational audit trail")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Get paginated audit logs")
    public ResponseEntity<PageResponse<AuditLogDto>> getAllAuditLogs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }
}
