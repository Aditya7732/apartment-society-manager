package com.society.manager.repository;

import com.society.manager.entity.Visitor;
import com.society.manager.enums.VisitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID>, JpaSpecificationExecutor<Visitor> {

    List<Visitor> findByFlatId(UUID flatId);

    List<Visitor> findByResidentId(UUID residentId);

    List<Visitor> findByStatus(VisitorStatus status);
}
