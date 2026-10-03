package com.society.manager.service;

import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.entity.Complaint;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Resident;
import com.society.manager.entity.SocietyExpense;

import java.time.LocalDate;
import java.util.List;

public interface PdfGeneratorService {
    byte[] generatePaymentReceiptPdf(PaymentDto paymentDto);
    byte[] generateMaintenanceBillPdf(MaintenanceBillDto billDto);
    byte[] generateFinancialReportPdf(List<MaintenanceBill> bills, LocalDate startDate, LocalDate endDate);
    byte[] generateResidentOccupancyReportPdf(List<Resident> residents);
    byte[] generateComplaintReportPdf(List<Complaint> complaints, LocalDate startDate, LocalDate endDate);
    byte[] generateExpenseReportPdf(List<SocietyExpense> expenses, LocalDate startDate, LocalDate endDate);
    byte[] generateDefaultersReportPdf(List<MaintenanceBill> defaulterBills);
}
