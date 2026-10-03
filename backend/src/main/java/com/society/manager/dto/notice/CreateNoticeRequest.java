package com.society.manager.dto.notice;

import com.society.manager.enums.AudienceType;
import com.society.manager.enums.NoticePriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNoticeRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Notice content is required")
    private String content;

    private NoticePriority priority = NoticePriority.MEDIUM;
    private AudienceType audience = AudienceType.ALL;
    private LocalDateTime expiryDate;
}
