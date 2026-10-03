package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.staff.CreateStaffRequest;
import com.society.manager.dto.staff.StaffDto;
import com.society.manager.enums.StaffRole;
import com.society.manager.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff Management", description = "Endpoints for managing society staff members (Security, Plumber, Electrician, Cleaner, etc.)")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Register new staff member")
    public ResponseEntity<StaffDto> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return new ResponseEntity<>(staffService.createStaff(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff details by ID")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable UUID id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @GetMapping("/active")
    @Operation(summary = "Get list of active staff members")
    public ResponseEntity<List<StaffDto>> getActiveStaff() {
        return ResponseEntity.ok(staffService.getActiveStaff());
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get list of staff by role (PLUMBER, ELECTRICIAN, etc.)")
    public ResponseEntity<List<StaffDto>> getStaffByRole(@PathVariable StaffRole role) {
        return ResponseEntity.ok(staffService.getStaffByRole(role));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of staff members")
    public ResponseEntity<PageResponse<StaffDto>> getAllStaff(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(staffService.getAllStaff(pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Toggle staff active status")
    public ResponseEntity<StaffDto> toggleStaffActive(@PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(staffService.toggleStaffActive(id, active));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Delete staff record")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
