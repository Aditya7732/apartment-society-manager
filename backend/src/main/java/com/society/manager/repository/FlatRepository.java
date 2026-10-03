package com.society.manager.repository;

import com.society.manager.entity.Flat;
import com.society.manager.enums.OccupancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlatRepository extends JpaRepository<Flat, UUID>, JpaSpecificationExecutor<Flat> {

    Optional<Flat> findByBuildingIdAndFlatNumber(UUID buildingId, String flatNumber);

    boolean existsByBuildingIdAndFlatNumber(UUID buildingId, String flatNumber);

    List<Flat> findByBuildingId(UUID buildingId);

    long countByOccupancyStatus(OccupancyStatus status);

    @Query("SELECT f.occupancyStatus, COUNT(f) FROM Flat f GROUP BY f.occupancyStatus")
    List<Object[]> countGroupByOccupancyStatus();
}
