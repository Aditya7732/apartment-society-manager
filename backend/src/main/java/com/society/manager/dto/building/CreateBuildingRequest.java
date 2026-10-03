package com.society.manager.dto.building;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBuildingRequest {
    @NotBlank(message = "Building name is required")
    private String name;

    @NotBlank(message = "Building code is required")
    private String code;

    @NotNull(message = "Total floors is required")
    @Min(value = 1, message = "Total floors must be at least 1")
    private Integer totalFloors;

    @NotNull(message = "Total flats is required")
    @Min(value = 1, message = "Total flats must be at least 1")
    private Integer totalFlats;

    private String description;

    public CreateBuildingRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
    public Integer getTotalFlats() { return totalFlats; }
    public void setTotalFlats(Integer totalFlats) { this.totalFlats = totalFlats; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
