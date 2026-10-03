package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.flat.CreateFlatRequest;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.enums.OccupancyStatus;
import com.society.manager.service.FlatService;
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
@RequestMapping("/api/flats")
@RequiredArgsConstructor
@Tag(name = "Flat Management", description = "Endpoints for managing society flats, occupancy status, and floor layout")
public class FlatController {

    private final FlatService flatService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Add new flat")
    public ResponseEntity<FlatDto> createFlat(@Valid @RequestBody CreateFlatRequest request) {
        return new ResponseEntity<>(flatService.createFlat(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flat details by ID")
    public ResponseEntity<FlatDto> getFlatById(@PathVariable UUID id) {
        return ResponseEntity.ok(flatService.getFlatById(id));
    }

    @GetMapping
    @Operation(summary = "Search and filter flats with pagination")
    public ResponseEntity<PageResponse<FlatDto>> searchFlats(
            @RequestParam(required = false) UUID buildingId,
            @RequestParam(required = false) Integer floorNumber,
            @RequestParam(required = false) OccupancyStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(flatService.searchFlats(buildingId, floorNumber, status, search, pageable));
    }

    @GetMapping("/building/{buildingId}")
    @Operation(summary = "Get list of flats by building ID")
    public ResponseEntity<List<FlatDto>> getFlatsByBuilding(@PathVariable UUID buildingId) {
        return ResponseEntity.ok(flatService.getFlatsByBuilding(buildingId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update flat information")
    public ResponseEntity<FlatDto> updateFlat(@PathVariable UUID id, @Valid @RequestBody CreateFlatRequest request) {
        return ResponseEntity.ok(flatService.updateFlat(id, request));
    }

    @PatchMapping("/{id}/occupancy-status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update occupancy status of flat")
    public ResponseEntity<FlatDto> updateOccupancyStatus(@PathVariable UUID id, @RequestParam OccupancyStatus status) {
        return ResponseEntity.ok(flatService.updateOccupancyStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Delete flat")
    public ResponseEntity<Void> deleteFlat(@PathVariable UUID id) {
        flatService.deleteFlat(id);
        return ResponseEntity.noContent().build();
    }
}
