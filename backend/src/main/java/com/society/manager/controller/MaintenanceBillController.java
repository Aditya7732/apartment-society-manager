package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.bill.BatchBillGenerationRequest;
import com.society.manager.dto.bill.CreateBillRequest;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.enums.BillStatus;
import com.society.manager.service.MaintenanceBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance-bills")
@RequiredArgsConstructor
@Tag(name = "Maintenance Billing", description = "Endpoints for generating and viewing monthly maintenance bills")
public class MaintenanceBillController {

    private final MaintenanceBillService billService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Generate a single maintenance bill")
    public ResponseEntity<MaintenanceBillDto> createBill(@Valid @RequestBody CreateBillRequest request) {
        return new ResponseEntity<>(billService.createBill(request), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Batch generate maintenance bills for multiple flats or entire building")
    public ResponseEntity<List<MaintenanceBillDto>> generateBatchBills(@Valid @RequestBody BatchBillGenerationRequest request) {
        return new ResponseEntity<>(billService.generateBatchBills(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill details by ID")
    public ResponseEntity<MaintenanceBillDto> getBillById(@PathVariable UUID id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @GetMapping
    @Operation(summary = "Search and filter maintenance bills with pagination")
    public ResponseEntity<PageResponse<MaintenanceBillDto>> searchBills(
            @RequestParam(required = false) UUID flatId,
            @RequestParam(required = false) String billingPeriod,
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(billService.searchBills(flatId, billingPeriod, status, search, pageable));
    }

    @GetMapping("/flat/{flatId}")
    @Operation(summary = "Get list of maintenance bills for a specific flat")
    public ResponseEntity<List<MaintenanceBillDto>> getBillsByFlat(@PathVariable UUID flatId) {
        return ResponseEntity.ok(billService.getBillsByFlat(flatId));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download official maintenance bill invoice PDF")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable UUID id) {
        byte[] pdfBytes = billService.downloadBillPdf(id);
        MaintenanceBillDto bill = billService.getBillById(id);
        String filename = "Bill-" + (bill.getBillNumber() != null ? bill.getBillNumber() : id.toString()) + ".pdf";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Cancel maintenance bill")
    public ResponseEntity<MaintenanceBillDto> cancelBill(@PathVariable UUID id) {
        return ResponseEntity.ok(billService.cancelBill(id));
    }
}
