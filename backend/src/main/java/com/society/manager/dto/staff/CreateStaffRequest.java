package com.society.manager.dto.staff;

import com.society.manager.enums.StaffRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateStaffRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Staff role is required")
    private StaffRole role;

    private LocalDate joiningDate;

    @PositiveOrZero(message = "Salary must be non-negative")
    private BigDecimal salary;

    private String emergencyContact;
    private boolean createAccount = false;
    private String username;
    private String email;
    private String password;
}
