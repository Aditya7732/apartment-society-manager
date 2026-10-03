package com.society.manager.dto.resident;

import java.util.UUID;

public class FamilyMemberDto {
    private UUID id;
    private UUID residentId;
    private String fullName;
    private String relation;
    private Integer age;
    private String phoneNumber;

    public FamilyMemberDto() {}

    public FamilyMemberDto(UUID id, UUID residentId, String fullName, String relation, Integer age, String phoneNumber) {
        this.id = id;
        this.residentId = residentId;
        this.fullName = fullName;
        this.relation = relation;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    public static FamilyMemberDtoBuilder builder() { return new FamilyMemberDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public static class FamilyMemberDtoBuilder {
        private UUID id;
        private UUID residentId;
        private String fullName;
        private String relation;
        private Integer age;
        private String phoneNumber;

        public FamilyMemberDtoBuilder id(UUID id) { this.id = id; return this; }
        public FamilyMemberDtoBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public FamilyMemberDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public FamilyMemberDtoBuilder relation(String relation) { this.relation = relation; return this; }
        public FamilyMemberDtoBuilder age(Integer age) { this.age = age; return this; }
        public FamilyMemberDtoBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }

        public FamilyMemberDto build() {
            return new FamilyMemberDto(id, residentId, fullName, relation, age, phoneNumber);
        }
    }
}
