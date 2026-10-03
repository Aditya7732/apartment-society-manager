package com.society.manager.entity;

import com.society.manager.enums.ExpenseCategory;
import com.society.manager.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "society_expenses")
public class SocietyExpense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "vendor_name", length = 100)
    private String vendorName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "attachment_path", length = 255)
    private String attachmentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public SocietyExpense() {}

    public SocietyExpense(UUID id, ExpenseCategory category, BigDecimal amount, LocalDate expenseDate, String vendorName, String description, PaymentMethod paymentMethod, String invoiceNumber, String attachmentPath, User createdBy) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.vendorName = vendorName;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.invoiceNumber = invoiceNumber;
        this.attachmentPath = attachmentPath;
        this.createdBy = createdBy;
    }

    public static SocietyExpenseBuilder builder() { return new SocietyExpenseBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public static class SocietyExpenseBuilder {
        private UUID id;
        private ExpenseCategory category;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String vendorName;
        private String description;
        private PaymentMethod paymentMethod;
        private String invoiceNumber;
        private String attachmentPath;
        private User createdBy;

        public SocietyExpenseBuilder id(UUID id) { this.id = id; return this; }
        public SocietyExpenseBuilder category(ExpenseCategory category) { this.category = category; return this; }
        public SocietyExpenseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public SocietyExpenseBuilder expenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; return this; }
        public SocietyExpenseBuilder vendorName(String vendorName) { this.vendorName = vendorName; return this; }
        public SocietyExpenseBuilder description(String description) { this.description = description; return this; }
        public SocietyExpenseBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public SocietyExpenseBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public SocietyExpenseBuilder attachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; return this; }
        public SocietyExpenseBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }

        public SocietyExpense build() {
            return new SocietyExpense(id, category, amount, expenseDate, vendorName, description, paymentMethod, invoiceNumber, attachmentPath, createdBy);
        }
    }
}
