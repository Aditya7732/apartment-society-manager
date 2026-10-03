package com.society.manager.specification;

import com.society.manager.entity.Complaint;
import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ComplaintSpecification {

    public static Specification<Complaint> filterComplaints(UUID residentId, UUID flatId, UUID assignedStaffId,
                                                             ComplaintCategory category, ComplaintPriority priority,
                                                             ComplaintStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (residentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("resident").get("id"), residentId));
            }

            if (flatId != null) {
                predicates.add(criteriaBuilder.equal(root.get("flat").get("id"), flatId));
            }

            if (assignedStaffId != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedStaff").get("id"), assignedStaffId));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm);
                Predicate flatNumMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("flat").get("flatNumber")), searchTerm);
                predicates.add(criteriaBuilder.or(titleMatch, descMatch, flatNumMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
