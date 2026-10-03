package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.complaint.*;
import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.ComplaintService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Management", description = "Endpoints for resident complaint registration, staff assignment, resolution, and comments")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Submit a new complaint (Residents only)")
    public ResponseEntity<ComplaintDto> createComplaint(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateComplaintRequest request) {
        return new ResponseEntity<>(complaintService.createComplaint(request, currentUser.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint details by ID")
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable UUID id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @GetMapping
    @Operation(summary = "Search and filter complaints with pagination")
    public ResponseEntity<PageResponse<ComplaintDto>> searchComplaints(
            @RequestParam(required = false) UUID residentId,
            @RequestParam(required = false) UUID flatId,
            @RequestParam(required = false) UUID assignedStaffId,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) ComplaintPriority priority,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(complaintService.searchComplaints(residentId, flatId, assignedStaffId, category, priority, status, search, pageable));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Assign complaint to a staff member")
    public ResponseEntity<ComplaintDto> assignStaff(@PathVariable UUID id, @Valid @RequestBody AssignComplaintRequest request) {
        return ResponseEntity.ok(complaintService.assignStaff(id, request.getStaffId()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update complaint status and resolution details")
    public ResponseEntity<ComplaintDto> updateStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateComplaintStatusRequest request) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(complaintService.updateStatus(id, request, userId));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a complaint timeline")
    public ResponseEntity<ComplaintCommentDto> addComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AddCommentRequest request) {
        return new ResponseEntity<>(complaintService.addComment(id, request, currentUser.getId()), HttpStatus.CREATED);
    }
}
