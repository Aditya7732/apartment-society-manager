package com.society.manager.specification;

import com.society.manager.entity.Resident;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResidentSpecification {

    public static Specification<Resident> filterResidents(UUID buildingId, Boolean active, Boolean isOwner, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (buildingId != null) {
                predicates.add(criteriaBuilder.equal(root.get("flat").get("building").get("id"), buildingId));
            }

            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            if (isOwner != null) {
                predicates.add(criteriaBuilder.equal(root.get("isOwner"), isOwner));
            }

            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm);
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm);
                Predicate emailMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm);
                Predicate phoneMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), searchTerm);
                Predicate flatNumberMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("flat").get("flatNumber")), searchTerm);
                predicates.add(criteriaBuilder.or(firstNameMatch, lastNameMatch, emailMatch, phoneMatch, flatNumberMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
