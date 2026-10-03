package com.society.manager.dto.building;

import java.time.LocalDateTime;
import java.util.UUID;

public class BuildingDto {
    private UUID id;
    private String name;
    private String code;
    private Integer totalFloors;
    private Integer totalFlats;
    private String description;
    private LocalDateTime createdAt;

    public BuildingDto() {}

    public BuildingDto(UUID id, String name, String code, Integer totalFloors, Integer totalFlats, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.totalFloors = totalFloors;
        this.totalFlats = totalFlats;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static BuildingDtoBuilder builder() { return new BuildingDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class BuildingDtoBuilder {
        private UUID id;
        private String name;
        private String code;
        private Integer totalFloors;
        private Integer totalFlats;
        private String description;
        private LocalDateTime createdAt;

        public BuildingDtoBuilder id(UUID id) { this.id = id; return this; }
        public BuildingDtoBuilder name(String name) { this.name = name; return this; }
        public BuildingDtoBuilder code(String code) { this.code = code; return this; }
        public BuildingDtoBuilder totalFloors(Integer totalFloors) { this.totalFloors = totalFloors; return this; }
        public BuildingDtoBuilder totalFlats(Integer totalFlats) { this.totalFlats = totalFlats; return this; }
        public BuildingDtoBuilder description(String description) { this.description = description; return this; }
        public BuildingDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public BuildingDto build() {
            return new BuildingDto(id, name, code, totalFloors, totalFlats, description, createdAt);
        }
    }
}
