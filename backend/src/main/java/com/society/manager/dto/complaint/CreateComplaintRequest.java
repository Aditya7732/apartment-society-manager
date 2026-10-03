package com.society.manager.dto.complaint;

import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateComplaintRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private ComplaintCategory category;

    private ComplaintPriority priority = ComplaintPriority.MEDIUM;
}
