package com.society.manager.service;

import com.society.manager.dto.building.BuildingDto;
import com.society.manager.dto.building.CreateBuildingRequest;

import java.util.List;
import java.util.UUID;

public interface BuildingService {
    BuildingDto createBuilding(CreateBuildingRequest request);
    BuildingDto getBuildingById(UUID id);
    List<BuildingDto> getAllBuildings();
    BuildingDto updateBuilding(UUID id, CreateBuildingRequest request);
    void deleteBuilding(UUID id);
}
