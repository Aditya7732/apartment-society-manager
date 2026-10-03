package com.society.manager.controller;

import com.society.manager.dto.dashboard.AdminDashboardDto;
import com.society.manager.dto.dashboard.ResidentDashboardDto;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Analytics", description = "Endpoints for Admin/Manager dashboard metrics and Resident dashboard summary")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Get admin executive dashboard metrics, charts data, and live statistics")
    public ResponseEntity<AdminDashboardDto> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/resident")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get resident dashboard summary (bills, complaints, notices, visitors)")
    public ResponseEntity<ResidentDashboardDto> getResidentDashboard(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(dashboardService.getResidentDashboard(currentUser.getId()));
    }
}
