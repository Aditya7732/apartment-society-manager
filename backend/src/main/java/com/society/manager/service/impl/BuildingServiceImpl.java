package com.society.manager.service.impl;

import com.society.manager.dto.building.BuildingDto;
import com.society.manager.dto.building.CreateBuildingRequest;
import com.society.manager.entity.Building;
import com.society.manager.exception.DuplicateResourceException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.BuildingRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public BuildingDto createBuilding(CreateBuildingRequest request) {
        if (buildingRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Building with name '" + request.getName() + "' already exists");
        }
        if (buildingRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Building with code '" + request.getCode() + "' already exists");
        }

        Building building = Building.builder()
                .name(request.getName())
                .code(request.getCode())
                .totalFloors(request.getTotalFloors())
                .totalFlats(request.getTotalFlats())
                .description(request.getDescription())
                .build();

        Building saved = buildingRepository.save(building);
        auditLogService.logAction(null, "CREATE_BUILDING", "BUILDING", saved.getId().toString(), null, null, "Building created: " + saved.getName());
        return entityMapper.toBuildingDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingDto getBuildingById(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
        return entityMapper.toBuildingDto(building);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll()
                .stream()
                .map(entityMapper::toBuildingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BuildingDto updateBuilding(UUID id, CreateBuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        building.setName(request.getName());
        building.setCode(request.getCode());
        building.setTotalFloors(request.getTotalFloors());
        building.setTotalFlats(request.getTotalFlats());
        building.setDescription(request.getDescription());

        Building updated = buildingRepository.save(building);
        auditLogService.logAction(null, "UPDATE_BUILDING", "BUILDING", id.toString(), null, null, "Building updated: " + updated.getName());
        return entityMapper.toBuildingDto(updated);
    }

    @Override
    @Transactional
    public void deleteBuilding(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
        buildingRepository.delete(building);
        auditLogService.logAction(null, "DELETE_BUILDING", "BUILDING", id.toString(), null, "Building deleted: " + building.getName(), null);
    }
}
