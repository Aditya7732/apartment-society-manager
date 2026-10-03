package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.staff.CreateStaffRequest;
import com.society.manager.dto.staff.StaffDto;
import com.society.manager.enums.StaffRole;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface StaffService {
    StaffDto createStaff(CreateStaffRequest request);
    StaffDto getStaffById(UUID id);
    List<StaffDto> getActiveStaff();
    List<StaffDto> getStaffByRole(StaffRole role);
    PageResponse<StaffDto> getAllStaff(Pageable pageable);
    StaffDto toggleStaffActive(UUID id, boolean active);
    void deleteStaff(UUID id);
}
