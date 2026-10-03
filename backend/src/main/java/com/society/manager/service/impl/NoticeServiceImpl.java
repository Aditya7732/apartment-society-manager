package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.notice.CreateNoticeRequest;
import com.society.manager.dto.notice.NoticeDto;
import com.society.manager.entity.Notice;
import com.society.manager.entity.User;
import com.society.manager.enums.AudienceType;
import com.society.manager.enums.NoticePriority;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.NoticeRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public NoticeDto createNotice(CreateNoticeRequest request, UUID currentUserId) {
        User createdBy = null;
        if (currentUserId != null) {
            createdBy = userRepository.findById(currentUserId).orElse(null);
        }

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .priority(request.getPriority() != null ? request.getPriority() : NoticePriority.MEDIUM)
                .audience(request.getAudience() != null ? request.getAudience() : AudienceType.ALL)
                .publishDate(LocalDateTime.now())
                .expiryDate(request.getExpiryDate())
                .createdBy(createdBy)
                .build();

        Notice saved = noticeRepository.save(notice);
        auditLogService.logAction(currentUserId, "CREATE_NOTICE", "NOTICE", saved.getId().toString(), null, null, "Notice published: " + saved.getTitle());
        return entityMapper.toNoticeDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDto getNoticeById(UUID id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));
        return entityMapper.toNoticeDto(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> getActiveNotices() {
        return noticeRepository.findActiveNotices(LocalDateTime.now())
                .stream()
                .map(entityMapper::toNoticeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NoticeDto> getAllNotices(Pageable pageable) {
        Page<Notice> page = noticeRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(entityMapper::toNoticeDto));
    }

    @Override
    @Transactional
    public void deleteNotice(UUID id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));
        noticeRepository.delete(notice);
        auditLogService.logAction(null, "DELETE_NOTICE", "NOTICE", id.toString(), null, "Notice deleted: " + notice.getTitle(), null);
    }
}
