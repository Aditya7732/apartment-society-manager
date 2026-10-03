package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.flat.CreateFlatRequest;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.enums.OccupancyStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FlatService {
    FlatDto createFlat(CreateFlatRequest request);
    FlatDto getFlatById(UUID id);
    PageResponse<FlatDto> searchFlats(UUID buildingId, Integer floorNumber, OccupancyStatus status, String search, Pageable pageable);
    List<FlatDto> getFlatsByBuilding(UUID buildingId);
    FlatDto updateFlat(UUID id, CreateFlatRequest request);
    FlatDto updateOccupancyStatus(UUID id, OccupancyStatus status);
    void deleteFlat(UUID id);
}
