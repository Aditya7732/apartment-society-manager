package com.society.manager.entity;

import com.society.manager.enums.PaymentMethod;
import com.society.manager.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private MaintenanceBill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.SUCCESS;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    public Payment(UUID id, MaintenanceBill bill, Resident resident, String receiptNumber, BigDecimal amount, LocalDateTime paymentDate, PaymentMethod paymentMethod, String transactionId, PaymentStatus status, String notes, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.bill = bill;
        this.resident = resident;
        this.receiptNumber = receiptNumber;
        this.amount = amount;
        if (paymentDate != null) this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        if (status != null) this.status = status;
        this.notes = notes;
        this.createdBy = createdBy;
        if (createdAt != null) this.createdAt = createdAt;
    }

    public static PaymentBuilder builder() { return new PaymentBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public MaintenanceBill getBill() { return bill; }
    public void setBill(MaintenanceBill bill) { this.bill = bill; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class PaymentBuilder {
        private UUID id;
        private MaintenanceBill bill;
        private Resident resident;
        private String receiptNumber;
        private BigDecimal amount;
        private LocalDateTime paymentDate = LocalDateTime.now();
        private PaymentMethod paymentMethod;
        private String transactionId;
        private PaymentStatus status = PaymentStatus.SUCCESS;
        private String notes;
        private User createdBy;
        private LocalDateTime createdAt = LocalDateTime.now();

        public PaymentBuilder id(UUID id) { this.id = id; return this; }
        public PaymentBuilder bill(MaintenanceBill bill) { this.bill = bill; return this; }
        public PaymentBuilder resident(Resident resident) { this.resident = resident; return this; }
        public PaymentBuilder receiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; return this; }
        public PaymentBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentBuilder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }
        public PaymentBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public PaymentBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public PaymentBuilder status(PaymentStatus status) { this.status = status; return this; }
        public PaymentBuilder notes(String notes) { this.notes = notes; return this; }
        public PaymentBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public PaymentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Payment build() {
            return new Payment(id, bill, resident, receiptNumber, amount, paymentDate, paymentMethod, transactionId, status, notes, createdBy, createdAt);
        }
    }
}
