package com.society.manager.repository;

import com.society.manager.entity.MaintenanceBill;
import com.society.manager.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceBillRepository extends JpaRepository<MaintenanceBill, UUID>, JpaSpecificationExecutor<MaintenanceBill> {

    Optional<MaintenanceBill> findByFlatIdAndBillingPeriod(UUID flatId, String billingPeriod);

    boolean existsByFlatIdAndBillingPeriod(UUID flatId, String billingPeriod);

    boolean existsByBillNumber(String billNumber);

    List<MaintenanceBill> findByFlatId(UUID flatId);

    long countByStatus(BillStatus status);

    @Query("SELECT SUM(b.totalAmount - b.paidAmount) FROM MaintenanceBill b WHERE b.status IN ('GENERATED', 'PENDING', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal sumTotalOutstanding();

    @Query("SELECT b.billingPeriod, SUM(b.paidAmount), SUM(b.totalAmount) FROM MaintenanceBill b GROUP BY b.billingPeriod ORDER BY b.billingPeriod DESC")
    List<Object[]> getMonthlyCollectionSummary();

    @Query("SELECT SUM(b.paidAmount) FROM MaintenanceBill b WHERE b.billingPeriod = :period")
    BigDecimal sumPaidAmountByBillingPeriod(@Param("period") String period);
}
