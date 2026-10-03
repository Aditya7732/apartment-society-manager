package com.society.manager.controller;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.expense.CreateExpenseRequest;
import com.society.manager.dto.expense.SocietyExpenseDto;
import com.society.manager.enums.ExpenseCategory;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT')")
@Tag(name = "Society Expenses", description = "Endpoints for managing society vendor expenses, operational costs, and invoices")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Record society expense")
    public ResponseEntity<SocietyExpenseDto> createExpense(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateExpenseRequest request) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return new ResponseEntity<>(expenseService.createExpense(request, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense record by ID")
    public ResponseEntity<SocietyExpenseDto> getExpenseById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @GetMapping
    @Operation(summary = "Search society expenses with filtering and pagination")
    public ResponseEntity<PageResponse<SocietyExpenseDto>> searchExpenses(
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(expenseService.searchExpenses(category, fromDate, toDate, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense record")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
