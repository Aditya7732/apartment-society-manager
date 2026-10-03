package com.society.manager.dto.payment;

import com.society.manager.enums.PaymentMethod;
import com.society.manager.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDto {
    private UUID id;
    private UUID billId;
    private String billNumber;
    private String billingPeriod;
    private UUID residentId;
    private String residentName;
    private String flatNumber;
    private String receiptNumber;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private PaymentStatus status;
    private String notes;
    private String createdByName;

    public PaymentDto() {}

    public PaymentDto(UUID id, UUID billId, String billNumber, String billingPeriod, UUID residentId, String residentName, String flatNumber, String receiptNumber, BigDecimal amount, LocalDateTime paymentDate, PaymentMethod paymentMethod, String transactionId, PaymentStatus status, String notes, String createdByName) {
        this.id = id;
        this.billId = billId;
        this.billNumber = billNumber;
        this.billingPeriod = billingPeriod;
        this.residentId = residentId;
        this.residentName = residentName;
        this.flatNumber = flatNumber;
        this.receiptNumber = receiptNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.notes = notes;
        this.createdByName = createdByName;
    }

    public static PaymentDtoBuilder builder() { return new PaymentDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBillId() { return billId; }
    public void setBillId(UUID billId) { this.billId = billId; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public static class PaymentDtoBuilder {
        private UUID id;
        private UUID billId;
        private String billNumber;
        private String billingPeriod;
        private UUID residentId;
        private String residentName;
        private String flatNumber;
        private String receiptNumber;
        private BigDecimal amount;
        private LocalDateTime paymentDate;
        private PaymentMethod paymentMethod;
        private String transactionId;
        private PaymentStatus status;
        private String notes;
        private String createdByName;

        public PaymentDtoBuilder id(UUID id) { this.id = id; return this; }
        public PaymentDtoBuilder billId(UUID billId) { this.billId = billId; return this; }
        public PaymentDtoBuilder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public PaymentDtoBuilder billingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; return this; }
        public PaymentDtoBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public PaymentDtoBuilder residentName(String residentName) { this.residentName = residentName; return this; }
        public PaymentDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public PaymentDtoBuilder receiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; return this; }
        public PaymentDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentDtoBuilder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }
        public PaymentDtoBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public PaymentDtoBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public PaymentDtoBuilder status(PaymentStatus status) { this.status = status; return this; }
        public PaymentDtoBuilder notes(String notes) { this.notes = notes; return this; }
        public PaymentDtoBuilder createdByName(String createdByName) { this.createdByName = createdByName; return this; }

        public PaymentDto build() {
            return new PaymentDto(id, billId, billNumber, billingPeriod, residentId, residentName, flatNumber, receiptNumber, amount, paymentDate, paymentMethod, transactionId, status, notes, createdByName);
        }
    }
}
