package com.society.manager.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "buildings")
public class Building extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "total_floors", nullable = false)
    private Integer totalFloors;

    @Column(name = "total_flats", nullable = false)
    private Integer totalFlats;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Building() {}

    public Building(UUID id, String name, String code, Integer totalFloors, Integer totalFlats, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.totalFloors = totalFloors;
        this.totalFlats = totalFlats;
        this.description = description;
    }

    public static BuildingBuilder builder() { return new BuildingBuilder(); }

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

    public static class BuildingBuilder {
        private UUID id;
        private String name;
        private String code;
        private Integer totalFloors;
        private Integer totalFlats;
        private String description;

        public BuildingBuilder id(UUID id) { this.id = id; return this; }
        public BuildingBuilder name(String name) { this.name = name; return this; }
        public BuildingBuilder code(String code) { this.code = code; return this; }
        public BuildingBuilder totalFloors(Integer totalFloors) { this.totalFloors = totalFloors; return this; }
        public BuildingBuilder totalFlats(Integer totalFlats) { this.totalFlats = totalFlats; return this; }
        public BuildingBuilder description(String description) { this.description = description; return this; }

        public Building build() {
            return new Building(id, name, code, totalFloors, totalFlats, description);
        }
    }
}
