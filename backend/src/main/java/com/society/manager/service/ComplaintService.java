package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.complaint.*;
import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ComplaintService {
    ComplaintDto createComplaint(CreateComplaintRequest request, UUID residentUserId);
    ComplaintDto getComplaintById(UUID id);
    PageResponse<ComplaintDto> searchComplaints(UUID residentId, UUID flatId, UUID assignedStaffId,
                                                ComplaintCategory category, ComplaintPriority priority,
                                                ComplaintStatus status, String search, Pageable pageable);
    ComplaintDto assignStaff(UUID complaintId, UUID staffId);
    ComplaintDto updateStatus(UUID complaintId, UpdateComplaintStatusRequest request, UUID currentUserId);
    ComplaintCommentDto addComment(UUID complaintId, AddCommentRequest request, UUID userId);
}
