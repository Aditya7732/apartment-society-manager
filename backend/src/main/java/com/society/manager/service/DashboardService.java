package com.society.manager.service;

import com.society.manager.dto.dashboard.AdminDashboardDto;
import com.society.manager.dto.dashboard.ResidentDashboardDto;

import java.util.UUID;

public interface DashboardService {
    AdminDashboardDto getAdminDashboard();
    ResidentDashboardDto getResidentDashboard(UUID residentUserId);
}
