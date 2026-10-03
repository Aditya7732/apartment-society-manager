package com.society.manager.dto.staff;

import com.society.manager.enums.StaffRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class StaffDto {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String phone;
    private StaffRole role;
    private LocalDate joiningDate;
    private BigDecimal salary;
    private boolean active;
    private String emergencyContact;

    public StaffDto() {}

    public StaffDto(UUID id, UUID userId, String fullName, String phone, StaffRole role, LocalDate joiningDate, BigDecimal salary, boolean active, String emergencyContact) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.joiningDate = joiningDate;
        this.salary = salary;
        this.active = active;
        this.emergencyContact = emergencyContact;
    }

    public static StaffDtoBuilder builder() { return new StaffDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public static class StaffDtoBuilder {
        private UUID id;
        private UUID userId;
        private String fullName;
        private String phone;
        private StaffRole role;
        private LocalDate joiningDate;
        private BigDecimal salary;
        private boolean active;
        private String emergencyContact;

        public StaffDtoBuilder id(UUID id) { this.id = id; return this; }
        public StaffDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public StaffDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public StaffDtoBuilder phone(String phone) { this.phone = phone; return this; }
        public StaffDtoBuilder role(StaffRole role) { this.role = role; return this; }
        public StaffDtoBuilder joiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; return this; }
        public StaffDtoBuilder salary(BigDecimal salary) { this.salary = salary; return this; }
        public StaffDtoBuilder active(boolean active) { this.active = active; return this; }
        public StaffDtoBuilder emergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; return this; }

        public StaffDto build() {
            return new StaffDto(id, userId, fullName, phone, role, joiningDate, salary, active, emergencyContact);
        }
    }
}
