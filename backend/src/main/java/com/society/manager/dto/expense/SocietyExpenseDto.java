package com.society.manager.dto.expense;

import com.society.manager.enums.ExpenseCategory;
import com.society.manager.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SocietyExpenseDto {
    private UUID id;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String vendorName;
    private String description;
    private PaymentMethod paymentMethod;
    private String invoiceNumber;
    private String attachmentPath;
    private String createdByName;
    private LocalDateTime createdAt;

    public SocietyExpenseDto() {}

    public SocietyExpenseDto(UUID id, ExpenseCategory category, BigDecimal amount, LocalDate expenseDate, String vendorName, String description, PaymentMethod paymentMethod, String invoiceNumber, String attachmentPath, String createdByName, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.vendorName = vendorName;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.invoiceNumber = invoiceNumber;
        this.attachmentPath = attachmentPath;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
    }

    public static SocietyExpenseDtoBuilder builder() { return new SocietyExpenseDtoBuilder(); }

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
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class SocietyExpenseDtoBuilder {
        private UUID id;
        private ExpenseCategory category;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String vendorName;
        private String description;
        private PaymentMethod paymentMethod;
        private String invoiceNumber;
        private String attachmentPath;
        private String createdByName;
        private LocalDateTime createdAt;

        public SocietyExpenseDtoBuilder id(UUID id) { this.id = id; return this; }
        public SocietyExpenseDtoBuilder category(ExpenseCategory category) { this.category = category; return this; }
        public SocietyExpenseDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public SocietyExpenseDtoBuilder expenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; return this; }
        public SocietyExpenseDtoBuilder vendorName(String vendorName) { this.vendorName = vendorName; return this; }
        public SocietyExpenseDtoBuilder description(String description) { this.description = description; return this; }
        public SocietyExpenseDtoBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public SocietyExpenseDtoBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public SocietyExpenseDtoBuilder attachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; return this; }
        public SocietyExpenseDtoBuilder createdByName(String createdByName) { this.createdByName = createdByName; return this; }
        public SocietyExpenseDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SocietyExpenseDto build() {
            return new SocietyExpenseDto(id, category, amount, expenseDate, vendorName, description, paymentMethod, invoiceNumber, attachmentPath, createdByName, createdAt);
        }
    }
}
