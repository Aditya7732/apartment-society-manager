package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.flat.CreateFlatRequest;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.entity.Building;
import com.society.manager.entity.Flat;
import com.society.manager.entity.Resident;
import com.society.manager.enums.OccupancyStatus;
import com.society.manager.exception.DuplicateResourceException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.BuildingRepository;
import com.society.manager.repository.FlatRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.FlatService;
import com.society.manager.specification.FlatSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlatServiceImpl implements FlatService {

    private final FlatRepository flatRepository;
    private final BuildingRepository buildingRepository;
    private final ResidentRepository residentRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public FlatDto createFlat(CreateFlatRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + request.getBuildingId()));

        if (flatRepository.existsByBuildingIdAndFlatNumber(request.getBuildingId(), request.getFlatNumber())) {
            throw new DuplicateResourceException("Flat number '" + request.getFlatNumber() + "' already exists in " + building.getName());
        }

        Flat flat = Flat.builder()
                .building(building)
                .flatNumber(request.getFlatNumber())
                .floorNumber(request.getFloorNumber())
                .flatType(request.getFlatType())
                .areaSqft(request.getAreaSqft())
                .occupancyStatus(request.getOccupancyStatus() != null ? request.getOccupancyStatus() : OccupancyStatus.VACANT)
                .ownerName(request.getOwnerName())
                .ownerPhone(request.getOwnerPhone())
                .ownerEmail(request.getOwnerEmail())
                .parkingSlot(request.getParkingSlot())
                .build();

        Flat saved = flatRepository.save(flat);
        auditLogService.logAction(null, "CREATE_FLAT", "FLAT", saved.getId().toString(), null, null, "Flat created: " + saved.getFlatNumber());
        return mapToFlatDtoWithResident(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlatDto getFlatById(UUID id) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
        return mapToFlatDtoWithResident(flat);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FlatDto> searchFlats(UUID buildingId, Integer floorNumber, OccupancyStatus status, String search, Pageable pageable) {
        Specification<Flat> spec = FlatSpecification.filterFlats(buildingId, floorNumber, status, search);
        Page<Flat> page = flatRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(this::mapToFlatDtoWithResident));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlatDto> getFlatsByBuilding(UUID buildingId) {
        return flatRepository.findByBuildingId(buildingId)
                .stream()
                .map(this::mapToFlatDtoWithResident)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlatDto updateFlat(UUID id, CreateFlatRequest request) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));

        flat.setFlatNumber(request.getFlatNumber());
        flat.setFloorNumber(request.getFloorNumber());
        flat.setFlatType(request.getFlatType());
        flat.setAreaSqft(request.getAreaSqft());
        if (request.getOccupancyStatus() != null) {
            flat.setOccupancyStatus(request.getOccupancyStatus());
        }
        flat.setOwnerName(request.getOwnerName());
        flat.setOwnerPhone(request.getOwnerPhone());
        flat.setOwnerEmail(request.getOwnerEmail());
        flat.setParkingSlot(request.getParkingSlot());

        Flat updated = flatRepository.save(flat);
        auditLogService.logAction(null, "UPDATE_FLAT", "FLAT", id.toString(), null, null, "Flat details updated: " + updated.getFlatNumber());
        return mapToFlatDtoWithResident(updated);
    }

    @Override
    @Transactional
    public FlatDto updateOccupancyStatus(UUID id, OccupancyStatus status) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
        flat.setOccupancyStatus(status);
        Flat updated = flatRepository.save(flat);
        auditLogService.logAction(null, "UPDATE_FLAT_STATUS", "FLAT", id.toString(), null, null, "Occupancy status changed to: " + status);
        return mapToFlatDtoWithResident(updated);
    }

    @Override
    @Transactional
    public void deleteFlat(UUID id) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
        flatRepository.delete(flat);
        auditLogService.logAction(null, "DELETE_FLAT", "FLAT", id.toString(), null, "Flat deleted: " + flat.getFlatNumber(), null);
    }

    private FlatDto mapToFlatDtoWithResident(Flat flat) {
        FlatDto dto = entityMapper.toFlatDto(flat);
        Optional<Resident> residentOpt = residentRepository.findByFlatIdAndActiveTrue(flat.getId());
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            dto.setCurrentResidentName(resident.getFullName());
            dto.setCurrentResidentPhone(resident.getPhone());
        }
        return dto;
    }
}
