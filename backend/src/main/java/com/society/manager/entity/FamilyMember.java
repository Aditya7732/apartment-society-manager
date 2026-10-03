package com.society.manager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String relation;

    private Integer age;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FamilyMember() {}

    public FamilyMember(UUID id, Resident resident, String fullName, String relation, Integer age, String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.resident = resident;
        this.fullName = fullName;
        this.relation = relation;
        this.age = age;
        this.phoneNumber = phoneNumber;
        if (createdAt != null) this.createdAt = createdAt;
    }

    public static FamilyMemberBuilder builder() { return new FamilyMemberBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class FamilyMemberBuilder {
        private UUID id;
        private Resident resident;
        private String fullName;
        private String relation;
        private Integer age;
        private String phoneNumber;
        private LocalDateTime createdAt = LocalDateTime.now();

        public FamilyMemberBuilder id(UUID id) { this.id = id; return this; }
        public FamilyMemberBuilder resident(Resident resident) { this.resident = resident; return this; }
        public FamilyMemberBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public FamilyMemberBuilder relation(String relation) { this.relation = relation; return this; }
        public FamilyMemberBuilder age(Integer age) { this.age = age; return this; }
        public FamilyMemberBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public FamilyMemberBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FamilyMember build() {
            return new FamilyMember(id, resident, fullName, relation, age, phoneNumber, createdAt);
        }
    }
}
