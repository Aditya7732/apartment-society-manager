package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.visitor.RegisterVisitorRequest;
import com.society.manager.dto.visitor.VisitorDto;
import com.society.manager.enums.VisitorStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VisitorService {
    VisitorDto preRegisterVisitor(RegisterVisitorRequest request, UUID residentUserId);
    VisitorDto getVisitorById(UUID id);
    PageResponse<VisitorDto> searchVisitors(UUID flatId, VisitorStatus status, Pageable pageable);
    List<VisitorDto> getVisitorsByFlat(UUID flatId);
    VisitorDto checkInVisitor(UUID visitorId, UUID securityUserId);
    VisitorDto checkOutVisitor(UUID visitorId, UUID securityUserId);
    VisitorDto cancelVisitorRegistration(UUID visitorId);
}
