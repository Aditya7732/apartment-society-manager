package com.society.manager.dto.expense;

import com.society.manager.enums.ExpenseCategory;
import com.society.manager.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {
    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String vendorName;
    private String description;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String invoiceNumber;
}
