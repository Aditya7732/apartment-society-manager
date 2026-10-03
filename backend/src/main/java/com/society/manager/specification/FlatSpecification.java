package com.society.manager.specification;

import com.society.manager.entity.Flat;
import com.society.manager.enums.OccupancyStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlatSpecification {

    public static Specification<Flat> filterFlats(UUID buildingId, Integer floorNumber, OccupancyStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (buildingId != null) {
                predicates.add(criteriaBuilder.equal(root.get("building").get("id"), buildingId));
            }

            if (floorNumber != null) {
                predicates.add(criteriaBuilder.equal(root.get("floorNumber"), floorNumber));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("occupancyStatus"), status));
            }

            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                Predicate flatNumMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("flatNumber")), searchTerm);
                Predicate ownerNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerName")), searchTerm);
                Predicate ownerPhoneMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerPhone")), searchTerm);
                predicates.add(criteriaBuilder.or(flatNumMatch, ownerNameMatch, ownerPhoneMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
