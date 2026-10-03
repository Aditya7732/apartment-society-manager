package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.expense.CreateExpenseRequest;
import com.society.manager.dto.expense.SocietyExpenseDto;
import com.society.manager.entity.SocietyExpense;
import com.society.manager.entity.User;
import com.society.manager.enums.ExpenseCategory;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.SocietyExpenseRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.ExpenseService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final SocietyExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SocietyExpenseDto createExpense(CreateExpenseRequest request, UUID currentUserId) {
        User createdBy = null;
        if (currentUserId != null) {
            createdBy = userRepository.findById(currentUserId).orElse(null);
        }

        SocietyExpense expense = SocietyExpense.builder()
                .category(request.getCategory())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .vendorName(request.getVendorName())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .invoiceNumber(request.getInvoiceNumber())
                .createdBy(createdBy)
                .build();

        SocietyExpense saved = expenseRepository.save(expense);
        auditLogService.logAction(currentUserId, "CREATE_EXPENSE", "SOCIETY_EXPENSE", saved.getId().toString(), null, null, "Expense created: " + saved.getCategory() + " of ₹" + saved.getAmount());
        return entityMapper.toSocietyExpenseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietyExpenseDto getExpenseById(UUID id) {
        SocietyExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));
        return entityMapper.toSocietyExpenseDto(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SocietyExpenseDto> searchExpenses(ExpenseCategory category, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Specification<SocietyExpense> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), toDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SocietyExpense> page = expenseRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(entityMapper::toSocietyExpenseDto));
    }

    @Override
    @Transactional
    public void deleteExpense(UUID id) {
        SocietyExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));
        expenseRepository.delete(expense);
        auditLogService.logAction(null, "DELETE_EXPENSE", "SOCIETY_EXPENSE", id.toString(), null, "Deleted expense of amount ₹" + expense.getAmount(), null);
    }
}
