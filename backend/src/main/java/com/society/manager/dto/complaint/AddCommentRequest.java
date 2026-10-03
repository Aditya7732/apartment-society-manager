package com.society.manager.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
    @NotBlank(message = "Comment text is required")
    private String comment;
}
