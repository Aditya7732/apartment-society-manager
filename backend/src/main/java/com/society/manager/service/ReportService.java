package com.society.manager.service;

import java.time.LocalDate;

public interface ReportService {
    byte[] generateFinancialReportCsv(LocalDate startDate, LocalDate endDate);
    byte[] generateFinancialReportPdf(LocalDate startDate, LocalDate endDate);

    byte[] generateResidentOccupancyReportCsv();
    byte[] generateResidentOccupancyReportPdf();

    byte[] generateComplaintReportCsv(LocalDate startDate, LocalDate endDate);
    byte[] generateComplaintReportPdf(LocalDate startDate, LocalDate endDate);

    byte[] generateExpenseReportCsv(LocalDate startDate, LocalDate endDate);
    byte[] generateExpenseReportPdf(LocalDate startDate, LocalDate endDate);

    byte[] generateDefaultersReportCsv();
    byte[] generateDefaultersReportPdf();
}
