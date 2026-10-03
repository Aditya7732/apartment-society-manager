package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.notice.CreateNoticeRequest;
import com.society.manager.dto.notice.NoticeDto;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notices & Announcements", description = "Endpoints for broadcasting society notices and emergency announcements")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Publish a new society notice")
    public ResponseEntity<NoticeDto> createNotice(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateNoticeRequest request) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return new ResponseEntity<>(noticeService.createNotice(request, userId), HttpStatus.CREATED);
    }

    @GetMapping("/active")
    @Operation(summary = "Get list of active notices for residents")
    public ResponseEntity<List<NoticeDto>> getActiveNotices() {
        return ResponseEntity.ok(noticeService.getActiveNotices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notice details by ID")
    public ResponseEntity<NoticeDto> getNoticeById(@PathVariable UUID id) {
        return ResponseEntity.ok(noticeService.getNoticeById(id));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of all notices")
    public ResponseEntity<PageResponse<NoticeDto>> getAllNotices(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getAllNotices(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Delete notice")
    public ResponseEntity<Void> deleteNotice(@PathVariable UUID id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
