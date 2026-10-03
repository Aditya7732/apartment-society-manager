package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.visitor.RegisterVisitorRequest;
import com.society.manager.dto.visitor.VisitorDto;
import com.society.manager.entity.Flat;
import com.society.manager.entity.Resident;
import com.society.manager.entity.User;
import com.society.manager.entity.Visitor;
import com.society.manager.enums.NotificationType;
import com.society.manager.enums.VisitorStatus;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.FlatRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.repository.VisitorRepository;
import com.society.manager.security.SecurityUtils;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.NotificationService;
import com.society.manager.service.VisitorService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final FlatRepository flatRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public VisitorDto preRegisterVisitor(RegisterVisitorRequest request, UUID residentUserId) {
        Flat flat = flatRepository.findById(request.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + request.getFlatId()));

        Resident resident = null;
        if (residentUserId != null) {
            Optional<Resident> residentOpt = residentRepository.findByUserId(residentUserId);
            if (residentOpt.isPresent()) {
                resident = residentOpt.get();
            }
        }

        Visitor visitor = Visitor.builder()
                .flat(flat)
                .resident(resident)
                .visitorName(request.getVisitorName())
                .phoneNumber(request.getPhoneNumber())
                .purpose(request.getPurpose())
                .expectedArrival(request.getExpectedArrival())
                .expectedDeparture(request.getExpectedDeparture())
                .vehicleNumber(request.getVehicleNumber())
                .status(VisitorStatus.EXPECTED)
                .build();

        Visitor saved = visitorRepository.save(visitor);
        auditLogService.logAction(residentUserId, "PRE_REGISTER_VISITOR", "VISITOR", saved.getId().toString(), null, null, "Visitor registered: " + saved.getVisitorName() + " for flat " + flat.getFlatNumber());
        return entityMapper.toVisitorDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorDto getVisitorById(UUID id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor record not found with id: " + id));

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (visitor.getFlat() == null || resident.getFlat() == null || !visitor.getFlat().getId().equals(resident.getFlat().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view visitor logs for another flat.");
            }
        }

        return entityMapper.toVisitorDto(visitor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VisitorDto> searchVisitors(UUID flatId, VisitorStatus status, Pageable pageable) {
        UUID effectiveFlatId = flatId;
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            effectiveFlatId = resident.getFlat() != null ? resident.getFlat().getId() : null;
        }

        final UUID targetFlatId = effectiveFlatId;
        Specification<Visitor> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (targetFlatId != null) {
                predicates.add(cb.equal(root.get("flat").get("id"), targetFlatId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Visitor> page = visitorRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(entityMapper::toVisitorDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorDto> getVisitorsByFlat(UUID flatId) {
        return visitorRepository.findByFlatId(flatId)
                .stream()
                .map(entityMapper::toVisitorDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VisitorDto checkInVisitor(UUID visitorId, UUID securityUserId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor record not found with id: " + visitorId));

        User securityUser = null;
        if (securityUserId != null) {
            securityUser = userRepository.findById(securityUserId).orElse(null);
        }

        visitor.setStatus(VisitorStatus.CHECKED_IN);
        visitor.setActualArrival(LocalDateTime.now());
        visitor.setCheckedInBy(securityUser);

        Visitor updated = visitorRepository.save(visitor);

        // Notify resident
        if (visitor.getResident() != null && visitor.getResident().getUser() != null) {
            notificationService.createNotification(
                    visitor.getResident().getUser().getId(),
                    "Visitor Arrived at Gate",
                    "Your expected visitor " + visitor.getVisitorName() + " has checked in at society main gate.",
                    NotificationType.VISITOR,
                    visitor.getId().toString()
            );
        }

        auditLogService.logAction(securityUserId, "CHECK_IN_VISITOR", "VISITOR", visitorId.toString(), null, null, "Visitor checked in: " + updated.getVisitorName());
        return entityMapper.toVisitorDto(updated);
    }

    @Override
    @Transactional
    public VisitorDto checkOutVisitor(UUID visitorId, UUID securityUserId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor record not found with id: " + visitorId));

        User securityUser = null;
        if (securityUserId != null) {
            securityUser = userRepository.findById(securityUserId).orElse(null);
        }

        visitor.setStatus(VisitorStatus.CHECKED_OUT);
        visitor.setActualDeparture(LocalDateTime.now());
        visitor.setCheckedOutBy(securityUser);

        Visitor updated = visitorRepository.save(visitor);
        auditLogService.logAction(securityUserId, "CHECK_OUT_VISITOR", "VISITOR", visitorId.toString(), null, null, "Visitor checked out: " + updated.getVisitorName());
        return entityMapper.toVisitorDto(updated);
    }

    @Override
    @Transactional
    public VisitorDto cancelVisitorRegistration(UUID visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor record not found with id: " + visitorId));
        visitor.setStatus(VisitorStatus.CANCELLED);
        Visitor updated = visitorRepository.save(visitor);
        return entityMapper.toVisitorDto(updated);
    }
}
