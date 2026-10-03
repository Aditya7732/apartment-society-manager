package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.resident.*;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.ResidentService;
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
@RequestMapping("/api/residents")
@RequiredArgsConstructor
@Tag(name = "Resident Management", description = "Endpoints for managing society residents, profile, family members, and vehicles")
public class ResidentController {

    private final ResidentService residentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Register new resident")
    public ResponseEntity<ResidentDto> createResident(@Valid @RequestBody CreateResidentRequest request) {
        return new ResponseEntity<>(residentService.createResident(request), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get logged in resident's own profile")
    public ResponseEntity<ResidentDto> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(residentService.getResidentByUserId(currentUser.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resident details by ID")
    public ResponseEntity<ResidentDto> getResidentById(@PathVariable UUID id) {
        return ResponseEntity.ok(residentService.getResidentById(id));
    }

    @GetMapping
    @Operation(summary = "Search and filter residents with pagination")
    public ResponseEntity<PageResponse<ResidentDto>> searchResidents(
            @RequestParam(required = false) UUID buildingId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(residentService.searchResidents(buildingId, active, isOwner, search, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update resident profile")
    public ResponseEntity<ResidentDto> updateResident(@PathVariable UUID id, @Valid @RequestBody CreateResidentRequest request) {
        return ResponseEntity.ok(residentService.updateResident(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Deactivate/Move-out resident")
    public ResponseEntity<ResidentDto> deactivateResident(@PathVariable UUID id) {
        return ResponseEntity.ok(residentService.deactivateResident(id));
    }

    @PostMapping("/{residentId}/family-members")
    @Operation(summary = "Add family member to resident profile")
    public ResponseEntity<FamilyMemberDto> addFamilyMember(@PathVariable UUID residentId, @Valid @RequestBody FamilyMemberDto dto) {
        return new ResponseEntity<>(residentService.addFamilyMember(residentId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/family-members/{id}")
    @Operation(summary = "Remove family member")
    public ResponseEntity<Void> removeFamilyMember(@PathVariable UUID id) {
        residentService.removeFamilyMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{residentId}/vehicles")
    @Operation(summary = "Add vehicle to resident profile")
    public ResponseEntity<VehicleDto> addVehicle(@PathVariable UUID residentId, @Valid @RequestBody VehicleDto dto) {
        return new ResponseEntity<>(residentService.addVehicle(residentId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/vehicles/{id}")
    @Operation(summary = "Remove vehicle")
    public ResponseEntity<Void> removeVehicle(@PathVariable UUID id) {
        residentService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
