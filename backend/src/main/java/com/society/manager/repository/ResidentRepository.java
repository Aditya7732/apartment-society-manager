package com.society.manager.repository;

import com.society.manager.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, UUID>, JpaSpecificationExecutor<Resident> {
    Optional<Resident> findByUserId(UUID userId);
    Optional<Resident> findByFlatIdAndActiveTrue(UUID flatId);
    boolean existsByEmail(String email);
    boolean existsByFlatIdAndActiveTrue(UUID flatId);
}
