package com.society.manager.service.impl;

import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.dto.complaint.ComplaintDto;
import com.society.manager.dto.dashboard.AdminDashboardDto;
import com.society.manager.dto.dashboard.MonthlyFinancialChartDto;
import com.society.manager.dto.dashboard.ResidentDashboardDto;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.dto.notice.NoticeDto;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.dto.visitor.VisitorDto;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Resident;
import com.society.manager.enums.BillStatus;
import com.society.manager.enums.ComplaintStatus;
import com.society.manager.enums.OccupancyStatus;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.*;
import com.society.manager.service.DashboardService;
import com.society.manager.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FlatRepository flatRepository;
    private final ResidentRepository residentRepository;
    private final MaintenanceBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final ComplaintRepository complaintRepository;
    private final SocietyExpenseRepository expenseRepository;
    private final NoticeService noticeService;
    private final VisitorRepository visitorRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getAdminDashboard() {
        long totalFlats = flatRepository.count();
        long occupiedFlats = flatRepository.countByOccupancyStatus(OccupancyStatus.OWNER_OCCUPIED) +
                flatRepository.countByOccupancyStatus(OccupancyStatus.TENANT_OCCUPIED);
        long vacantFlats = flatRepository.countByOccupancyStatus(OccupancyStatus.VACANT);
        long underMaintenanceFlats = flatRepository.countByOccupancyStatus(OccupancyStatus.UNDER_MAINTENANCE);
        long totalResidents = residentRepository.count();

        BigDecimal pendingMaintenance = billRepository.sumTotalOutstanding();
        if (pendingMaintenance == null) pendingMaintenance = BigDecimal.ZERO;

        String currentMonthPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        BigDecimal collectionThisMonth = billRepository.sumPaidAmountByBillingPeriod(currentMonthPeriod);
        if (collectionThisMonth == null) collectionThisMonth = BigDecimal.ZERO;

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal expensesThisMonth = expenseRepository.sumTotalExpensesBetween(firstDayOfMonth, LocalDate.now());
        if (expensesThisMonth == null) expensesThisMonth = BigDecimal.ZERO;

        long openComplaints = complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long inProgressComplaints = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS) +
                complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
        long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.RESOLVED) +
                complaintRepository.countByStatus(ComplaintStatus.CLOSED);

        // Recent Payments
        List<PaymentDto> recentPayments = paymentRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "paymentDate")))
                .getContent().stream().map(entityMapper::toPaymentDto).collect(Collectors.toList());

        // Recent Complaints
        List<ComplaintDto> recentComplaints = complaintRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent().stream().map(entityMapper::toComplaintDto).collect(Collectors.toList());

        // Complaint Category Map
        Map<String, Long> complaintsByCategory = new HashMap<>();
        complaintRepository.countGroupByCategory().forEach(row -> {
            complaintsByCategory.put(row[0].toString(), (Long) row[1]);
        });

        // Complaint Status Map
        Map<String, Long> complaintsByStatus = new HashMap<>();
        complaintRepository.countGroupByStatus().forEach(row -> {
            complaintsByStatus.put(row[0].toString(), (Long) row[1]);
        });

        // Occupancy Map
        Map<String, Long> occupancyStats = new HashMap<>();
        flatRepository.countGroupByOccupancyStatus().forEach(row -> {
            occupancyStats.put(row[0].toString(), (Long) row[1]);
        });

        // Monthly Financials Chart (Monthly Collections vs Expenses)
        List<MonthlyFinancialChartDto> monthlyFinancials = new ArrayList<>();
        List<Object[]> collections = billRepository.getMonthlyCollectionSummary();
        List<Object[]> expenses = expenseRepository.getMonthlyExpenseSummary();

        Map<String, BigDecimal> expenseMap = new HashMap<>();
        for (Object[] row : expenses) {
            if (row[0] != null) {
                expenseMap.put(row[0].toString(), (BigDecimal) row[1]);
            }
        }

        for (Object[] row : collections) {
            if (row[0] != null) {
                String month = row[0].toString();
                BigDecimal coll = (BigDecimal) row[1];
                BigDecimal exp = expenseMap.getOrDefault(month, BigDecimal.ZERO);
                monthlyFinancials.add(new MonthlyFinancialChartDto(month, coll != null ? coll : BigDecimal.ZERO, exp));
            }
        }

        return AdminDashboardDto.builder()
                .totalFlats(totalFlats)
                .occupiedFlats(occupiedFlats)
                .vacantFlats(vacantFlats)
                .underMaintenanceFlats(underMaintenanceFlats)
                .totalResidents(totalResidents)
                .pendingMaintenanceAmount(pendingMaintenance)
                .collectionThisMonth(collectionThisMonth)
                .expensesThisMonth(expensesThisMonth)
                .openComplaintsCount(openComplaints)
                .inProgressComplaintsCount(inProgressComplaints)
                .resolvedComplaintsCount(resolvedComplaints)
                .recentPayments(recentPayments)
                .recentComplaints(recentComplaints)
                .monthlyFinancials(monthlyFinancials)
                .complaintsByCategory(complaintsByCategory)
                .complaintsByStatus(complaintsByStatus)
                .occupancyStats(occupancyStats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDashboardDto getResidentDashboard(UUID residentUserId) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident record not found for user: " + residentUserId));

        FlatDto flatDto = resident.getFlat() != null ? entityMapper.toFlatDto(resident.getFlat()) : null;
        UUID flatId = resident.getFlat() != null ? resident.getFlat().getId() : null;

        List<MaintenanceBill> bills = flatId != null ? billRepository.findByFlatId(flatId) : Collections.emptyList();
        BigDecimal totalOutstanding = bills.stream()
                .filter(b -> b.getStatus() != BillStatus.PAID && b.getStatus() != BillStatus.CANCELLED)
                .map(b -> b.getTotalAmount().subtract(b.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MaintenanceBillDto currentBill = bills.stream()
                .filter(b -> b.getStatus() != BillStatus.PAID && b.getStatus() != BillStatus.CANCELLED)
                .max(Comparator.comparing(MaintenanceBill::getBillDate))
                .map(entityMapper::toMaintenanceBillDto)
                .orElse(null);

        List<MaintenanceBillDto> recentBills = bills.stream()
                .sorted(Comparator.comparing(MaintenanceBill::getBillDate).reversed())
                .limit(5)
                .map(entityMapper::toMaintenanceBillDto)
                .collect(Collectors.toList());

        List<ComplaintDto> myComplaints = complaintRepository.findByResidentId(resident.getId())
                .stream()
                .map(entityMapper::toComplaintDto)
                .collect(Collectors.toList());

        List<NoticeDto> latestNotices = noticeService.getActiveNotices();

        List<VisitorDto> upcomingVisitors = flatId != null
                ? visitorRepository.findByFlatId(flatId)
                        .stream()
                        .map(entityMapper::toVisitorDto)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ResidentDashboardDto.builder()
                .flatDetails(flatDto)
                .currentBill(currentBill)
                .totalOutstandingAmount(totalOutstanding)
                .recentBills(recentBills)
                .myComplaints(myComplaints)
                .latestNotices(latestNotices)
                .upcomingVisitors(upcomingVisitors)
                .build();
    }
}
