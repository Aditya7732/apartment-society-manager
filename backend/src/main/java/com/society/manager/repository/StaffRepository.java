package com.society.manager.repository;

import com.society.manager.entity.Staff;
import com.society.manager.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByUserId(UUID userId);

    List<Staff> findByRole(StaffRole role);

    List<Staff> findByActiveTrue();
}
