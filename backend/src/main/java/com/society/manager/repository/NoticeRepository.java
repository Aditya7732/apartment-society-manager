package com.society.manager.repository;

import com.society.manager.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID>, JpaSpecificationExecutor<Notice> {

    @Query("SELECT n FROM Notice n WHERE n.expiryDate IS NULL OR n.expiryDate > :now ORDER BY n.publishDate DESC")
    List<Notice> findActiveNotices(LocalDateTime now);
}
