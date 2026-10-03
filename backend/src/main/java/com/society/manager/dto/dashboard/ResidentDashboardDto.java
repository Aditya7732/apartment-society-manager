package com.society.manager.dto.dashboard;

import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.dto.complaint.ComplaintDto;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.dto.notice.NoticeDto;
import com.society.manager.dto.visitor.VisitorDto;

import java.math.BigDecimal;
import java.util.List;

public class ResidentDashboardDto {
    private FlatDto flatDetails;
    private MaintenanceBillDto currentBill;
    private BigDecimal totalOutstandingAmount;
    private List<MaintenanceBillDto> recentBills;
    private List<ComplaintDto> myComplaints;
    private List<NoticeDto> latestNotices;
    private List<VisitorDto> upcomingVisitors;

    public ResidentDashboardDto() {}

    public ResidentDashboardDto(FlatDto flatDetails, MaintenanceBillDto currentBill, BigDecimal totalOutstandingAmount, List<MaintenanceBillDto> recentBills, List<ComplaintDto> myComplaints, List<NoticeDto> latestNotices, List<VisitorDto> upcomingVisitors) {
        this.flatDetails = flatDetails;
        this.currentBill = currentBill;
        this.totalOutstandingAmount = totalOutstandingAmount;
        this.recentBills = recentBills;
        this.myComplaints = myComplaints;
        this.latestNotices = latestNotices;
        this.upcomingVisitors = upcomingVisitors;
    }

    public static ResidentDashboardDtoBuilder builder() { return new ResidentDashboardDtoBuilder(); }

    public FlatDto getFlatDetails() { return flatDetails; }
    public void setFlatDetails(FlatDto flatDetails) { this.flatDetails = flatDetails; }
    public MaintenanceBillDto getCurrentBill() { return currentBill; }
    public void setCurrentBill(MaintenanceBillDto currentBill) { this.currentBill = currentBill; }
    public BigDecimal getTotalOutstandingAmount() { return totalOutstandingAmount; }
    public void setTotalOutstandingAmount(BigDecimal totalOutstandingAmount) { this.totalOutstandingAmount = totalOutstandingAmount; }
    public List<MaintenanceBillDto> getRecentBills() { return recentBills; }
    public void setRecentBills(List<MaintenanceBillDto> recentBills) { this.recentBills = recentBills; }
    public List<ComplaintDto> getMyComplaints() { return myComplaints; }
    public void setMyComplaints(List<ComplaintDto> myComplaints) { this.myComplaints = myComplaints; }
    public List<NoticeDto> getLatestNotices() { return latestNotices; }
    public void setLatestNotices(List<NoticeDto> latestNotices) { this.latestNotices = latestNotices; }
    public List<VisitorDto> getUpcomingVisitors() { return upcomingVisitors; }
    public void setUpcomingVisitors(List<VisitorDto> upcomingVisitors) { this.upcomingVisitors = upcomingVisitors; }

    public static class ResidentDashboardDtoBuilder {
        private FlatDto flatDetails;
        private MaintenanceBillDto currentBill;
        private BigDecimal totalOutstandingAmount;
        private List<MaintenanceBillDto> recentBills;
        private List<ComplaintDto> myComplaints;
        private List<NoticeDto> latestNotices;
        private List<VisitorDto> upcomingVisitors;

        public ResidentDashboardDtoBuilder flatDetails(FlatDto flatDetails) { this.flatDetails = flatDetails; return this; }
        public ResidentDashboardDtoBuilder currentBill(MaintenanceBillDto currentBill) { this.currentBill = currentBill; return this; }
        public ResidentDashboardDtoBuilder totalOutstandingAmount(BigDecimal totalOutstandingAmount) { this.totalOutstandingAmount = totalOutstandingAmount; return this; }
        public ResidentDashboardDtoBuilder recentBills(List<MaintenanceBillDto> recentBills) { this.recentBills = recentBills; return this; }
        public ResidentDashboardDtoBuilder myComplaints(List<ComplaintDto> myComplaints) { this.myComplaints = myComplaints; return this; }
        public ResidentDashboardDtoBuilder latestNotices(List<NoticeDto> latestNotices) { this.latestNotices = latestNotices; return this; }
        public ResidentDashboardDtoBuilder upcomingVisitors(List<VisitorDto> upcomingVisitors) { this.upcomingVisitors = upcomingVisitors; return this; }

        public ResidentDashboardDto build() {
            return new ResidentDashboardDto(flatDetails, currentBill, totalOutstandingAmount, recentBills, myComplaints, latestNotices, upcomingVisitors);
        }
    }
}
