package com.society.manager.service.impl;

import com.society.manager.entity.Complaint;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Resident;
import com.society.manager.entity.SocietyExpense;
import com.society.manager.enums.BillStatus;
import com.society.manager.repository.ComplaintRepository;
import com.society.manager.repository.MaintenanceBillRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.repository.SocietyExpenseRepository;
import com.society.manager.service.PdfGeneratorService;
import com.society.manager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MaintenanceBillRepository billRepository;
    private final ResidentRepository residentRepository;
    private final ComplaintRepository complaintRepository;
    private final SocietyExpenseRepository expenseRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateFinancialReportCsv(LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Bill Number,Billing Period,Flat Number,Bill Date,Due Date,Total Amount,Paid Amount,Outstanding,Status\n");

        List<MaintenanceBill> bills = getFilteredBills(startDate, endDate);
        for (MaintenanceBill bill : bills) {
            BigDecimal total = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            csv.append(String.format("%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%s\n",
                    bill.getBillNumber(),
                    bill.getBillingPeriod(),
                    bill.getFlat() != null ? bill.getFlat().getFlatNumber() : "N/A",
                    bill.getBillDate(),
                    bill.getDueDate(),
                    total,
                    paid,
                    total.subtract(paid),
                    bill.getStatus()
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateFinancialReportPdf(LocalDate startDate, LocalDate endDate) {
        List<MaintenanceBill> bills = getFilteredBills(startDate, endDate);
        return pdfGeneratorService.generateFinancialReportPdf(bills, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateResidentOccupancyReportCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("Resident ID,First Name,Last Name,Email,Phone,Flat Number,Building Name,Ownership Status,Move In Date,Active\n");

        List<Resident> residents = residentRepository.findAll();
        for (Resident r : residents) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    r.getId(),
                    r.getFirstName(),
                    r.getLastName(),
                    r.getEmail(),
                    r.getPhone(),
                    r.getFlat() != null ? r.getFlat().getFlatNumber() : "N/A",
                    r.getFlat() != null && r.getFlat().getBuilding() != null ? r.getFlat().getBuilding().getName() : "N/A",
                    r.isOwner() ? "OWNER" : "TENANT",
                    r.getMoveInDate(),
                    r.isActive() ? "YES" : "NO"
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateResidentOccupancyReportPdf() {
        List<Resident> residents = residentRepository.findAll();
        return pdfGeneratorService.generateResidentOccupancyReportPdf(residents);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateComplaintReportCsv(LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Complaint ID,Title,Category,Priority,Status,Resident Name,Flat Number,Assigned Staff,Created Date,Closed Date\n");

        List<Complaint> complaints = getFilteredComplaints(startDate, endDate);
        for (Complaint c : complaints) {
            csv.append(String.format("%s,\"%s\",%s,%s,%s,\"%s\",%s,\"%s\",%s,%s\n",
                    c.getId(),
                    c.getTitle().replace("\"", "\"\""),
                    c.getCategory(),
                    c.getPriority(),
                    c.getStatus(),
                    c.getResident() != null ? c.getResident().getFullName() : "N/A",
                    c.getFlat() != null ? c.getFlat().getFlatNumber() : "N/A",
                    c.getAssignedStaff() != null ? c.getAssignedStaff().getFullName() : "Unassigned",
                    c.getCreatedAt(),
                    c.getClosedDate() != null ? c.getClosedDate() : "N/A"
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateComplaintReportPdf(LocalDate startDate, LocalDate endDate) {
        List<Complaint> complaints = getFilteredComplaints(startDate, endDate);
        return pdfGeneratorService.generateComplaintReportPdf(complaints, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateExpenseReportCsv(LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Expense ID,Category,Amount,Vendor Name,Invoice Number,Expense Date,Payment Method,Description\n");

        List<SocietyExpense> expenses = getFilteredExpenses(startDate, endDate);
        for (SocietyExpense ex : expenses) {
            csv.append(String.format("%s,%s,%.2f,\"%s\",\"%s\",%s,%s,\"%s\"\n",
                    ex.getId(),
                    ex.getCategory(),
                    ex.getAmount() != null ? ex.getAmount() : BigDecimal.ZERO,
                    ex.getVendorName() != null ? ex.getVendorName().replace("\"", "\"\"") : "N/A",
                    ex.getInvoiceNumber() != null ? ex.getInvoiceNumber().replace("\"", "\"\"") : "N/A",
                    ex.getExpenseDate(),
                    ex.getPaymentMethod(),
                    ex.getDescription() != null ? ex.getDescription().replace("\"", "\"\"") : ""
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateExpenseReportPdf(LocalDate startDate, LocalDate endDate) {
        List<SocietyExpense> expenses = getFilteredExpenses(startDate, endDate);
        return pdfGeneratorService.generateExpenseReportPdf(expenses, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateDefaultersReportCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("Bill Number,Flat Number,Billing Period,Due Date,Total Amount,Paid Amount,Outstanding Amount,Status\n");

        List<MaintenanceBill> defaulters = getDefaulterBills();
        for (MaintenanceBill bill : defaulters) {
            BigDecimal total = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            csv.append(String.format("%s,%s,%s,%s,%.2f,%.2f,%.2f,%s\n",
                    bill.getBillNumber(),
                    bill.getFlat() != null ? bill.getFlat().getFlatNumber() : "N/A",
                    bill.getBillingPeriod(),
                    bill.getDueDate(),
                    total,
                    paid,
                    total.subtract(paid),
                    bill.getStatus()
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateDefaultersReportPdf() {
        List<MaintenanceBill> defaulters = getDefaulterBills();
        return pdfGeneratorService.generateDefaultersReportPdf(defaulters);
    }

    private List<MaintenanceBill> getFilteredBills(LocalDate startDate, LocalDate endDate) {
        return billRepository.findAll().stream()
                .filter(b -> (startDate == null || !b.getBillDate().isBefore(startDate)) &&
                             (endDate == null || !b.getBillDate().isAfter(endDate)))
                .collect(Collectors.toList());
    }

    private List<Complaint> getFilteredComplaints(LocalDate startDate, LocalDate endDate) {
        return complaintRepository.findAll().stream()
                .filter(c -> {
                    if (startDate != null && c.getCreatedAt() != null && c.getCreatedAt().toLocalDate().isBefore(startDate)) return false;
                    if (endDate != null && c.getCreatedAt() != null && c.getCreatedAt().toLocalDate().isAfter(endDate)) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<SocietyExpense> getFilteredExpenses(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findAll().stream()
                .filter(e -> (startDate == null || !e.getExpenseDate().isBefore(startDate)) &&
                             (endDate == null || !e.getExpenseDate().isAfter(endDate)))
                .collect(Collectors.toList());
    }

    private List<MaintenanceBill> getDefaulterBills() {
        return billRepository.findAll().stream()
                .filter(b -> b.getStatus() == BillStatus.OVERDUE ||
                            ((b.getStatus() == BillStatus.PENDING || b.getStatus() == BillStatus.PARTIALLY_PAID) &&
                             b.getDueDate() != null && b.getDueDate().isBefore(LocalDate.now())))
                .collect(Collectors.toList());
    }
}
