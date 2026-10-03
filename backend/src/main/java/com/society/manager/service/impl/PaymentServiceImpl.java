package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.dto.payment.RecordPaymentRequest;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Payment;
import com.society.manager.entity.Resident;
import com.society.manager.entity.User;
import com.society.manager.enums.BillStatus;
import com.society.manager.enums.NotificationType;
import com.society.manager.enums.PaymentStatus;
import com.society.manager.exception.BadRequestException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.MaintenanceBillRepository;
import com.society.manager.repository.PaymentRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.security.SecurityUtils;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.NotificationService;
import com.society.manager.service.PaymentService;
import com.society.manager.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MaintenanceBillRepository billRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public PaymentDto recordPayment(RecordPaymentRequest request, UUID currentUserId) {
        MaintenanceBill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + request.getBillId()));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BadRequestException("Bill is already fully paid.");
        }
        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay a cancelled bill.");
        }

        BigDecimal outstanding = bill.getTotalAmount().subtract(bill.getPaidAmount()).max(BigDecimal.ZERO);
        if (request.getAmount().compareTo(outstanding) > 0) {
            throw new BadRequestException("Payment amount (₹" + request.getAmount() + ") exceeds remaining outstanding bill amount (₹" + outstanding + ").");
        }

        User createdBy = null;
        if (currentUserId != null) {
            createdBy = userRepository.findById(currentUserId).orElse(null);
        }

        // Find resident associated with the flat
        Resident resident = null;
        if (bill.getFlat() != null) {
            Optional<Resident> residentOpt = residentRepository.findByFlatIdAndActiveTrue(bill.getFlat().getId());
            if (residentOpt.isPresent()) {
                resident = residentOpt.get();
            }
        }

        String timeStampStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String receiptNumber = "REC-" + timeStampStr + "-" + (int)(Math.random() * 9000 + 1000);

        Payment payment = Payment.builder()
                .bill(bill)
                .resident(resident)
                .receiptNumber(receiptNumber)
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getTransactionId())
                .status(PaymentStatus.SUCCESS)
                .notes(request.getNotes())
                .createdBy(createdBy)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update bill status and paid amount
        BigDecimal newPaidAmount = bill.getPaidAmount().add(request.getAmount());
        bill.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }
        billRepository.save(bill);

        // Notify resident
        if (resident != null && resident.getUser() != null) {
            notificationService.createNotification(
                    resident.getUser().getId(),
                    "Payment Received Confirmation",
                    "Payment of ₹" + savedPayment.getAmount() + " received for bill " + bill.getBillNumber() + ". Receipt #: " + savedPayment.getReceiptNumber() + ".",
                    NotificationType.PAYMENT,
                    savedPayment.getId().toString()
            );
        }

        auditLogService.logAction(currentUserId, "RECORD_PAYMENT", "PAYMENT", savedPayment.getId().toString(), null, null, "Payment recorded: Receipt " + receiptNumber + " of amount ₹" + request.getAmount());

        return entityMapper.toPaymentDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + id));

        verifyPaymentResidentAccess(payment);
        return entityMapper.toPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByReceiptNumber(String receiptNumber) {
        Payment payment = paymentRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with receipt #: " + receiptNumber));

        verifyPaymentResidentAccess(payment);
        return entityMapper.toPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentDto> getAllPayments(Pageable pageable) {
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            Page<Payment> page = paymentRepository.findByResidentId(resident.getId(), pageable);
            return PageResponse.fromPage(page.map(entityMapper::toPaymentDto));
        }

        Page<Payment> page = paymentRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(entityMapper::toPaymentDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByBill(UUID billId) {
        MaintenanceBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (bill.getFlat() == null || resident.getFlat() == null || !bill.getFlat().getId().equals(resident.getFlat().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view payments for another flat's bill.");
            }
        }

        return paymentRepository.findByBillId(billId)
                .stream()
                .map(entityMapper::toPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByResident(UUID residentId) {
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (!resident.getId().equals(residentId)) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another resident's payments.");
            }
        }

        return paymentRepository.findByResidentId(residentId)
                .stream()
                .map(entityMapper::toPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadPaymentReceiptPdf(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));

        verifyPaymentResidentAccess(payment);
        PaymentDto paymentDto = entityMapper.toPaymentDto(payment);
        return pdfGeneratorService.generatePaymentReceiptPdf(paymentDto);
    }

    private void verifyPaymentResidentAccess(Payment payment) {
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            boolean isOwnResident = payment.getResident() != null && payment.getResident().getId().equals(resident.getId());
            boolean isOwnFlat = payment.getBill() != null && payment.getBill().getFlat() != null
                    && resident.getFlat() != null
                    && payment.getBill().getFlat().getId().equals(resident.getFlat().getId());
            if (!isOwnResident && !isOwnFlat) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to access another resident's payment receipt.");
            }
        }
    }
}
