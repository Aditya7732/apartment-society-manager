package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.resident.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ResidentService {
    ResidentDto createResident(CreateResidentRequest request);
    ResidentDto getResidentById(UUID id);
    ResidentDto getResidentByUserId(UUID userId);
    PageResponse<ResidentDto> searchResidents(UUID buildingId, Boolean active, Boolean isOwner, String search, Pageable pageable);
    ResidentDto updateResident(UUID id, CreateResidentRequest request);
    ResidentDto deactivateResident(UUID id);
    
    // Family member & vehicle management
    FamilyMemberDto addFamilyMember(UUID residentId, FamilyMemberDto familyMemberDto);
    void removeFamilyMember(UUID familyMemberId);
    VehicleDto addVehicle(UUID residentId, VehicleDto vehicleDto);
    void removeVehicle(UUID vehicleId);
}
