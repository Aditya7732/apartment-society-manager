package com.society.manager.dto.bill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateBillRequest {
    @NotNull(message = "Flat ID is required")
    private UUID flatId;

    @NotBlank(message = "Billing period is required (e.g. YYYY-MM)")
    private String billingPeriod;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Base amount is required")
    @PositiveOrZero(message = "Base amount must be zero or positive")
    private BigDecimal baseAmount;

    private BigDecimal parkingCharges = BigDecimal.ZERO;
    private BigDecimal waterCharges = BigDecimal.ZERO;
    private BigDecimal lateFee = BigDecimal.ZERO;
    private BigDecimal otherCharges = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private String notes;
}
