package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.expense.CreateExpenseRequest;
import com.society.manager.dto.expense.SocietyExpenseDto;
import com.society.manager.enums.ExpenseCategory;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ExpenseService {
    SocietyExpenseDto createExpense(CreateExpenseRequest request, UUID currentUserId);
    SocietyExpenseDto getExpenseById(UUID id);
    PageResponse<SocietyExpenseDto> searchExpenses(ExpenseCategory category, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    void deleteExpense(UUID id);
}
