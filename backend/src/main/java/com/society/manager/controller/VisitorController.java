package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.visitor.RegisterVisitorRequest;
import com.society.manager.dto.visitor.VisitorDto;
import com.society.manager.enums.VisitorStatus;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.VisitorService;
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
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Tag(name = "Visitor Management", description = "Endpoints for resident pre-registration of visitors and gate security check-in/check-out log")
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/pre-register")
    @Operation(summary = "Pre-register an expected visitor")
    public ResponseEntity<VisitorDto> preRegisterVisitor(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody RegisterVisitorRequest request) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return new ResponseEntity<>(visitorService.preRegisterVisitor(request, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visitor details by ID")
    public ResponseEntity<VisitorDto> getVisitorById(@PathVariable UUID id) {
        return ResponseEntity.ok(visitorService.getVisitorById(id));
    }

    @GetMapping
    @Operation(summary = "Search visitor logs with filtering and pagination")
    public ResponseEntity<PageResponse<VisitorDto>> searchVisitors(
            @RequestParam(required = false) UUID flatId,
            @RequestParam(required = false) VisitorStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(visitorService.searchVisitors(flatId, status, pageable));
    }

    @GetMapping("/flat/{flatId}")
    @Operation(summary = "Get visitor log for a specific flat")
    public ResponseEntity<List<VisitorDto>> getVisitorsByFlat(@PathVariable UUID flatId) {
        return ResponseEntity.ok(visitorService.getVisitorsByFlat(flatId));
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'SECURITY')")
    @Operation(summary = "Security gate check-in for visitor")
    public ResponseEntity<VisitorDto> checkInVisitor(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(visitorService.checkInVisitor(id, userId));
    }

    @PatchMapping("/{id}/check-out")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'SECURITY')")
    @Operation(summary = "Security gate check-out for visitor")
    public ResponseEntity<VisitorDto> checkOutVisitor(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(visitorService.checkOutVisitor(id, userId));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel expected visitor registration")
    public ResponseEntity<VisitorDto> cancelVisitorRegistration(@PathVariable UUID id) {
        return ResponseEntity.ok(visitorService.cancelVisitorRegistration(id));
    }
}
