package com.society.manager.dto.resident;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateResidentRequest {
    private UUID userId; // Optional: Link to existing user account

    @NotNull(message = "Flat ID is required")
    private UUID flatId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String emergencyContactName;
    private String emergencyContactPhone;

    @NotNull(message = "Move-in date is required")
    private LocalDate moveInDate;

    private boolean isOwner = false;
    private boolean createAccount = true; // Auto create resident user account if userId not provided
}
