package com.society.manager.controller;

import com.society.manager.dto.building.BuildingDto;
import com.society.manager.dto.building.CreateBuildingRequest;
import com.society.manager.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
@Tag(name = "Building Management", description = "Endpoints for managing society buildings and blocks")
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Add new building")
    public ResponseEntity<BuildingDto> createBuilding(@Valid @RequestBody CreateBuildingRequest request) {
        return new ResponseEntity<>(buildingService.createBuilding(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building details by ID")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @GetMapping
    @Operation(summary = "Get list of all buildings")
    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Update building details")
    public ResponseEntity<BuildingDto> updateBuilding(@PathVariable UUID id, @Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Delete building")
    public ResponseEntity<Void> deleteBuilding(@PathVariable UUID id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
