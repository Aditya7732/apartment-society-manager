package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.notice.CreateNoticeRequest;
import com.society.manager.dto.notice.NoticeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NoticeService {
    NoticeDto createNotice(CreateNoticeRequest request, UUID currentUserId);
    NoticeDto getNoticeById(UUID id);
    List<NoticeDto> getActiveNotices();
    PageResponse<NoticeDto> getAllNotices(Pageable pageable);
    void deleteNotice(UUID id);
}
