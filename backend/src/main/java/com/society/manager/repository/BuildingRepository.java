package com.society.manager.repository;

import com.society.manager.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<Building, UUID>, JpaSpecificationExecutor<Building> {
    Optional<Building> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
