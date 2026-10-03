package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.dto.payment.RecordPaymentRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentDto recordPayment(RecordPaymentRequest request, UUID currentUserId);
    PaymentDto getPaymentById(UUID id);
    PaymentDto getPaymentByReceiptNumber(String receiptNumber);
    PageResponse<PaymentDto> getAllPayments(Pageable pageable);
    List<PaymentDto> getPaymentsByBill(UUID billId);
    List<PaymentDto> getPaymentsByResident(UUID residentId);
    byte[] downloadPaymentReceiptPdf(UUID paymentId);
}
