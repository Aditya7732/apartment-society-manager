package com.society.manager.repository;

import com.society.manager.entity.SocietyExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SocietyExpenseRepository extends JpaRepository<SocietyExpense, UUID>, JpaSpecificationExecutor<SocietyExpense> {

    @Query("SELECT SUM(e.amount) FROM SocietyExpense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalExpensesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category, SUM(e.amount) FROM SocietyExpense e GROUP BY e.category")
    List<Object[]> getExpenseSummaryByCategory();

    @Query("SELECT FUNCTION('TO_CHAR', e.expenseDate, 'YYYY-MM'), SUM(e.amount) FROM SocietyExpense e GROUP BY FUNCTION('TO_CHAR', e.expenseDate, 'YYYY-MM') ORDER BY 1 DESC")
    List<Object[]> getMonthlyExpenseSummary();
}
