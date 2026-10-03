package com.society.manager.controller;

import com.society.manager.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
@Tag(name = "Reports & Analytics", description = "Endpoints for generating exportable PDF and CSV reports for financial, resident, expense, and complaint data")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial/csv")
    @Operation(summary = "Export financial maintenance collection report as CSV")
    public ResponseEntity<byte[]> exportFinancialReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] csvData = reportService.generateFinancialReportCsv(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Financial_Collection_Report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/financial/pdf")
    @Operation(summary = "Export financial maintenance collection report as PDF")
    public ResponseEntity<byte[]> exportFinancialReportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfData = reportService.generateFinancialReportPdf(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Financial_Collection_Report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/residents/csv")
    @Operation(summary = "Export resident occupancy report as CSV")
    public ResponseEntity<byte[]> exportResidentOccupancyReportCsv() {
        byte[] csvData = reportService.generateResidentOccupancyReportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Resident_Occupancy_Report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/residents/pdf")
    @Operation(summary = "Export resident occupancy report as PDF")
    public ResponseEntity<byte[]> exportResidentOccupancyReportPdf() {
        byte[] pdfData = reportService.generateResidentOccupancyReportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Resident_Occupancy_Report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/complaints/csv")
    @Operation(summary = "Export complaint resolution report as CSV")
    public ResponseEntity<byte[]> exportComplaintReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] csvData = reportService.generateComplaintReportCsv(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Helpdesk_Complaints_Report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/complaints/pdf")
    @Operation(summary = "Export complaint resolution report as PDF")
    public ResponseEntity<byte[]> exportComplaintReportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfData = reportService.generateComplaintReportPdf(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Helpdesk_Complaints_Report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/expenses/csv")
    @Operation(summary = "Export society expense ledger as CSV")
    public ResponseEntity<byte[]> exportExpenseReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] csvData = reportService.generateExpenseReportCsv(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Society_Expense_Ledger.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/expenses/pdf")
    @Operation(summary = "Export society expense ledger as PDF")
    public ResponseEntity<byte[]> exportExpenseReportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfData = reportService.generateExpenseReportPdf(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Society_Expense_Ledger.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/defaulters/csv")
    @Operation(summary = "Export overdue defaulters list as CSV")
    public ResponseEntity<byte[]> exportDefaultersReportCsv() {
        byte[] csvData = reportService.generateDefaultersReportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Overdue_Defaulters_Report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/defaulters/pdf")
    @Operation(summary = "Export overdue defaulters list as PDF")
    public ResponseEntity<byte[]> exportDefaultersReportPdf() {
        byte[] pdfData = reportService.generateDefaultersReportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Overdue_Defaulters_Report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
