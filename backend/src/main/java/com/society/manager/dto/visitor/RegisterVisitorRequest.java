package com.society.manager.dto.visitor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RegisterVisitorRequest {
    @NotNull(message = "Flat ID is required")
    private UUID flatId;

    @NotBlank(message = "Visitor name is required")
    private String visitorName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    @NotNull(message = "Expected arrival time is required")
    private LocalDateTime expectedArrival;

    private LocalDateTime expectedDeparture;
    private String vehicleNumber;
}
