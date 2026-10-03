package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.complaint.*;
import com.society.manager.entity.*;
import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;
import com.society.manager.enums.NotificationType;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.*;
import com.society.manager.security.SecurityUtils;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.ComplaintService;
import com.society.manager.service.NotificationService;
import com.society.manager.specification.ComplaintSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCommentRepository commentRepository;
    private final ResidentRepository residentRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ComplaintDto createComplaint(CreateComplaintRequest request, UUID residentUserId) {
        Resident resident = residentRepository.findByUserId(residentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident profile not found for current user"));

        Complaint complaint = Complaint.builder()
                .resident(resident)
                .flat(resident.getFlat())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority() != null ? request.getPriority() : ComplaintPriority.MEDIUM)
                .status(ComplaintStatus.OPEN)
                .build();

        Complaint saved = complaintRepository.save(complaint);
        auditLogService.logAction(residentUserId, "CREATE_COMPLAINT", "COMPLAINT", saved.getId().toString(), null, null, "Complaint submitted: " + saved.getTitle());
        return entityMapper.toComplaintDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintDto getComplaintById(UUID id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (complaint.getResident() == null || !complaint.getResident().getId().equals(resident.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another resident's complaint.");
            }
        }

        return entityMapper.toComplaintDto(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ComplaintDto> searchComplaints(UUID residentId, UUID flatId, UUID assignedStaffId,
                                                        ComplaintCategory category, ComplaintPriority priority,
                                                        ComplaintStatus status, String search, Pageable pageable) {
        UUID effectiveResidentId = residentId;
        UUID effectiveFlatId = flatId;

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            effectiveResidentId = resident.getId();
            effectiveFlatId = resident.getFlat() != null ? resident.getFlat().getId() : null;
        }

        Specification<Complaint> spec = ComplaintSpecification.filterComplaints(
                effectiveResidentId, effectiveFlatId, assignedStaffId, category, priority, status, search
        );
        Page<Complaint> page = complaintRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(entityMapper::toComplaintDto));
    }

    @Override
    @Transactional
    public ComplaintDto assignStaff(UUID complaintId, UUID staffId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));

        complaint.setAssignedStaff(staff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint updated = complaintRepository.save(complaint);

        // Notify resident
        if (complaint.getResident() != null && complaint.getResident().getUser() != null) {
            notificationService.createNotification(
                    complaint.getResident().getUser().getId(),
                    "Complaint Assigned",
                    "Your complaint '" + complaint.getTitle() + "' has been assigned to " + staff.getFullName() + " (" + staff.getRole() + ").",
                    NotificationType.COMPLAINT,
                    complaint.getId().toString()
            );
        }

        auditLogService.logAction(null, "ASSIGN_COMPLAINT", "COMPLAINT", complaintId.toString(), null, null, "Assigned to staff: " + staff.getFullName());
        return entityMapper.toComplaintDto(updated);
    }

    @Override
    @Transactional
    public ComplaintDto updateStatus(UUID complaintId, UpdateComplaintStatusRequest request, UUID currentUserId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(request.getStatus());

        if (request.getResolution() != null) {
            complaint.setResolution(request.getResolution());
        }

        if (request.getStatus() == ComplaintStatus.RESOLVED || request.getStatus() == ComplaintStatus.CLOSED) {
            complaint.setClosedDate(LocalDateTime.now());
        }

        Complaint updated = complaintRepository.save(complaint);

        // Notify resident
        if (complaint.getResident() != null && complaint.getResident().getUser() != null) {
            notificationService.createNotification(
                    complaint.getResident().getUser().getId(),
                    "Complaint Status Updated",
                    "Your complaint '" + complaint.getTitle() + "' status changed from " + oldStatus + " to " + request.getStatus() + ".",
                    NotificationType.COMPLAINT,
                    complaint.getId().toString()
            );
        }

        auditLogService.logAction(currentUserId, "UPDATE_COMPLAINT_STATUS", "COMPLAINT", complaintId.toString(), null, oldStatus.name(), request.getStatus().name());
        return entityMapper.toComplaintDto(updated);
    }

    @Override
    @Transactional
    public ComplaintCommentDto addComment(UUID complaintId, AddCommentRequest request, UUID userId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ComplaintComment comment = ComplaintComment.builder()
                .complaint(complaint)
                .user(user)
                .comment(request.getComment())
                .build();

        ComplaintComment saved = commentRepository.save(comment);
        return entityMapper.toComplaintCommentDto(saved);
    }
}
