package com.society.manager.dto.bill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BatchBillGenerationRequest {
    private UUID buildingId; // Optional: generate only for a specific building, or all flats if null

    @NotBlank(message = "Billing period is required (e.g. 2026-04)")
    private String billingPeriod;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Base amount is required")
    @PositiveOrZero
    private BigDecimal baseAmount;

    private BigDecimal defaultParkingCharges = BigDecimal.ZERO;
    private BigDecimal defaultWaterCharges = BigDecimal.ZERO;
    private String notes;
}
