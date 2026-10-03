package com.society.manager.dto.complaint;

import com.society.manager.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateComplaintStatusRequest {
    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    private String resolution;
}
