package com.society.manager.specification;

import com.society.manager.entity.MaintenanceBill;
import com.society.manager.enums.BillStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillSpecification {

    public static Specification<MaintenanceBill> filterBills(UUID flatId, String billingPeriod, BillStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (flatId != null) {
                predicates.add(criteriaBuilder.equal(root.get("flat").get("id"), flatId));
            }

            if (StringUtils.hasText(billingPeriod)) {
                predicates.add(criteriaBuilder.equal(root.get("billingPeriod"), billingPeriod));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                Predicate billNumMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("billNumber")), searchTerm);
                Predicate flatNumMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("flat").get("flatNumber")), searchTerm);
                predicates.add(criteriaBuilder.or(billNumMatch, flatNumMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
