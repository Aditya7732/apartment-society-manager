package com.society.manager.dto.dashboard;

import com.society.manager.dto.complaint.ComplaintDto;
import com.society.manager.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdminDashboardDto {
    private long totalFlats;
    private long occupiedFlats;
    private long vacantFlats;
    private long underMaintenanceFlats;
    private long totalResidents;

    private BigDecimal pendingMaintenanceAmount;
    private BigDecimal collectionThisMonth;
    private BigDecimal expensesThisMonth;

    private long openComplaintsCount;
    private long inProgressComplaintsCount;
    private long resolvedComplaintsCount;

    private List<PaymentDto> recentPayments;
    private List<ComplaintDto> recentComplaints;

    private List<MonthlyFinancialChartDto> monthlyFinancials;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByStatus;
    private Map<String, Long> occupancyStats;

    public AdminDashboardDto() {}

    public AdminDashboardDto(long totalFlats, long occupiedFlats, long vacantFlats, long underMaintenanceFlats, long totalResidents, BigDecimal pendingMaintenanceAmount, BigDecimal collectionThisMonth, BigDecimal expensesThisMonth, long openComplaintsCount, long inProgressComplaintsCount, long resolvedComplaintsCount, List<PaymentDto> recentPayments, List<ComplaintDto> recentComplaints, List<MonthlyFinancialChartDto> monthlyFinancials, Map<String, Long> complaintsByCategory, Map<String, Long> complaintsByStatus, Map<String, Long> occupancyStats) {
        this.totalFlats = totalFlats;
        this.occupiedFlats = occupiedFlats;
        this.vacantFlats = vacantFlats;
        this.underMaintenanceFlats = underMaintenanceFlats;
        this.totalResidents = totalResidents;
        this.pendingMaintenanceAmount = pendingMaintenanceAmount;
        this.collectionThisMonth = collectionThisMonth;
        this.expensesThisMonth = expensesThisMonth;
        this.openComplaintsCount = openComplaintsCount;
        this.inProgressComplaintsCount = inProgressComplaintsCount;
        this.resolvedComplaintsCount = resolvedComplaintsCount;
        this.recentPayments = recentPayments;
        this.recentComplaints = recentComplaints;
        this.monthlyFinancials = monthlyFinancials;
        this.complaintsByCategory = complaintsByCategory;
        this.complaintsByStatus = complaintsByStatus;
        this.occupancyStats = occupancyStats;
    }

    public static AdminDashboardDtoBuilder builder() { return new AdminDashboardDtoBuilder(); }

    public long getTotalFlats() { return totalFlats; }
    public void setTotalFlats(long totalFlats) { this.totalFlats = totalFlats; }
    public long getOccupiedFlats() { return occupiedFlats; }
    public void setOccupiedFlats(long occupiedFlats) { this.occupiedFlats = occupiedFlats; }
    public long getVacantFlats() { return vacantFlats; }
    public void setVacantFlats(long vacantFlats) { this.vacantFlats = vacantFlats; }
    public long getUnderMaintenanceFlats() { return underMaintenanceFlats; }
    public void setUnderMaintenanceFlats(long underMaintenanceFlats) { this.underMaintenanceFlats = underMaintenanceFlats; }
    public long getTotalResidents() { return totalResidents; }
    public void setTotalResidents(long totalResidents) { this.totalResidents = totalResidents; }
    public BigDecimal getPendingMaintenanceAmount() { return pendingMaintenanceAmount; }
    public void setPendingMaintenanceAmount(BigDecimal pendingMaintenanceAmount) { this.pendingMaintenanceAmount = pendingMaintenanceAmount; }
    public BigDecimal getCollectionThisMonth() { return collectionThisMonth; }
    public void setCollectionThisMonth(BigDecimal collectionThisMonth) { this.collectionThisMonth = collectionThisMonth; }
    public BigDecimal getExpensesThisMonth() { return expensesThisMonth; }
    public void setExpensesThisMonth(BigDecimal expensesThisMonth) { this.expensesThisMonth = expensesThisMonth; }
    public long getOpenComplaintsCount() { return openComplaintsCount; }
    public void setOpenComplaintsCount(long openComplaintsCount) { this.openComplaintsCount = openComplaintsCount; }
    public long getInProgressComplaintsCount() { return inProgressComplaintsCount; }
    public void setInProgressComplaintsCount(long inProgressComplaintsCount) { this.inProgressComplaintsCount = inProgressComplaintsCount; }
    public long getResolvedComplaintsCount() { return resolvedComplaintsCount; }
    public void setResolvedComplaintsCount(long resolvedComplaintsCount) { this.resolvedComplaintsCount = resolvedComplaintsCount; }
    public List<PaymentDto> getRecentPayments() { return recentPayments; }
    public void setRecentPayments(List<PaymentDto> recentPayments) { this.recentPayments = recentPayments; }
    public List<ComplaintDto> getRecentComplaints() { return recentComplaints; }
    public void setRecentComplaints(List<ComplaintDto> recentComplaints) { this.recentComplaints = recentComplaints; }
    public List<MonthlyFinancialChartDto> getMonthlyFinancials() { return monthlyFinancials; }
    public void setMonthlyFinancials(List<MonthlyFinancialChartDto> monthlyFinancials) { this.monthlyFinancials = monthlyFinancials; }
    public Map<String, Long> getComplaintsByCategory() { return complaintsByCategory; }
    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; }
    public Map<String, Long> getComplaintsByStatus() { return complaintsByStatus; }
    public void setComplaintsByStatus(Map<String, Long> complaintsByStatus) { this.complaintsByStatus = complaintsByStatus; }
    public Map<String, Long> getOccupancyStats() { return occupancyStats; }
    public void setOccupancyStats(Map<String, Long> occupancyStats) { this.occupancyStats = occupancyStats; }

    public static class AdminDashboardDtoBuilder {
        private long totalFlats;
        private long occupiedFlats;
        private long vacantFlats;
        private long underMaintenanceFlats;
        private long totalResidents;
        private BigDecimal pendingMaintenanceAmount;
        private BigDecimal collectionThisMonth;
        private BigDecimal expensesThisMonth;
        private long openComplaintsCount;
        private long inProgressComplaintsCount;
        private long resolvedComplaintsCount;
        private List<PaymentDto> recentPayments;
        private List<ComplaintDto> recentComplaints;
        private List<MonthlyFinancialChartDto> monthlyFinancials;
        private Map<String, Long> complaintsByCategory;
        private Map<String, Long> complaintsByStatus;
        private Map<String, Long> occupancyStats;

        public AdminDashboardDtoBuilder totalFlats(long totalFlats) { this.totalFlats = totalFlats; return this; }
        public AdminDashboardDtoBuilder occupiedFlats(long occupiedFlats) { this.occupiedFlats = occupiedFlats; return this; }
        public AdminDashboardDtoBuilder vacantFlats(long vacantFlats) { this.vacantFlats = vacantFlats; return this; }
        public AdminDashboardDtoBuilder underMaintenanceFlats(long underMaintenanceFlats) { this.underMaintenanceFlats = underMaintenanceFlats; return this; }
        public AdminDashboardDtoBuilder totalResidents(long totalResidents) { this.totalResidents = totalResidents; return this; }
        public AdminDashboardDtoBuilder pendingMaintenanceAmount(BigDecimal pendingMaintenanceAmount) { this.pendingMaintenanceAmount = pendingMaintenanceAmount; return this; }
        public AdminDashboardDtoBuilder collectionThisMonth(BigDecimal collectionThisMonth) { this.collectionThisMonth = collectionThisMonth; return this; }
        public AdminDashboardDtoBuilder expensesThisMonth(BigDecimal expensesThisMonth) { this.expensesThisMonth = expensesThisMonth; return this; }
        public AdminDashboardDtoBuilder openComplaintsCount(long openComplaintsCount) { this.openComplaintsCount = openComplaintsCount; return this; }
        public AdminDashboardDtoBuilder inProgressComplaintsCount(long inProgressComplaintsCount) { this.inProgressComplaintsCount = inProgressComplaintsCount; return this; }
        public AdminDashboardDtoBuilder resolvedComplaintsCount(long resolvedComplaintsCount) { this.resolvedComplaintsCount = resolvedComplaintsCount; return this; }
        public AdminDashboardDtoBuilder recentPayments(List<PaymentDto> recentPayments) { this.recentPayments = recentPayments; return this; }
        public AdminDashboardDtoBuilder recentComplaints(List<ComplaintDto> recentComplaints) { this.recentComplaints = recentComplaints; return this; }
        public AdminDashboardDtoBuilder monthlyFinancials(List<MonthlyFinancialChartDto> monthlyFinancials) { this.monthlyFinancials = monthlyFinancials; return this; }
        public AdminDashboardDtoBuilder complaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; return this; }
        public AdminDashboardDtoBuilder complaintsByStatus(Map<String, Long> complaintsByStatus) { this.complaintsByStatus = complaintsByStatus; return this; }
        public AdminDashboardDtoBuilder occupancyStats(Map<String, Long> occupancyStats) { this.occupancyStats = occupancyStats; return this; }

        public AdminDashboardDto build() {
            return new AdminDashboardDto(totalFlats, occupiedFlats, vacantFlats, underMaintenanceFlats, totalResidents, pendingMaintenanceAmount, collectionThisMonth, expensesThisMonth, openComplaintsCount, inProgressComplaintsCount, resolvedComplaintsCount, recentPayments, recentComplaints, monthlyFinancials, complaintsByCategory, complaintsByStatus, occupancyStats);
        }
    }
}
