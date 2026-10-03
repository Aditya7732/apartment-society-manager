package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.dto.payment.RecordPaymentRequest;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for recording payments, viewing history, and downloading PDF receipts")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Record maintenance bill payment")
    public ResponseEntity<PaymentDto> recordPayment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody RecordPaymentRequest request) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return new ResponseEntity<>(paymentService.recordPayment(request, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment record by ID")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/receipt/{receiptNumber}")
    @Operation(summary = "Get payment record by receipt number")
    public ResponseEntity<PaymentDto> getPaymentByReceiptNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByReceiptNumber(receiptNumber));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of all recorded payments")
    public ResponseEntity<PageResponse<PaymentDto>> getAllPayments(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }

    @GetMapping("/bill/{billId}")
    @Operation(summary = "Get payments for a specific bill")
    public ResponseEntity<List<PaymentDto>> getPaymentsByBill(@PathVariable UUID billId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBill(billId));
    }

    @GetMapping("/resident/{residentId}")
    @Operation(summary = "Get payments for a specific resident")
    public ResponseEntity<List<PaymentDto>> getPaymentsByResident(@PathVariable UUID residentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByResident(residentId));
    }

    @GetMapping("/{id}/receipt/pdf")
    @Operation(summary = "Download official payment receipt PDF")
    public ResponseEntity<byte[]> downloadPaymentReceiptPdf(@PathVariable UUID id) {
        byte[] pdfBytes = paymentService.downloadPaymentReceiptPdf(id);
        PaymentDto payment = paymentService.getPaymentById(id);
        String filename = "Receipt-" + (payment.getReceiptNumber() != null ? payment.getReceiptNumber() : payment.getId()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
