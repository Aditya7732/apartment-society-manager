package com.society.manager.entity;

import com.society.manager.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false, unique = true)
    private RoleType name;

    public Role() {}

    public Role(Long id, RoleType name) {
        this.id = id;
        this.name = name;
    }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoleType getName() { return name; }
    public void setName(RoleType name) { this.name = name; }

    public static class RoleBuilder {
        private Long id;
        private RoleType name;

        public RoleBuilder id(Long id) { this.id = id; return this; }
        public RoleBuilder name(RoleType name) { this.name = name; return this; }
        public Role build() { return new Role(id, name); }
    }
}
