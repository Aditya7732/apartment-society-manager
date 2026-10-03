package com.society.manager.dto.resident;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ResidentDto {
    private UUID id;
    private UUID userId;
    private String username;
    private UUID flatId;
    private String flatNumber;
    private String buildingName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private boolean isOwner;
    private boolean active;
    private List<FamilyMemberDto> familyMembers;
    private List<VehicleDto> vehicles;

    public ResidentDto() {}

    public ResidentDto(UUID id, UUID userId, String username, UUID flatId, String flatNumber, String buildingName, String firstName, String lastName, String fullName, String email, String phone, String emergencyContactName, String emergencyContactPhone, LocalDate moveInDate, LocalDate moveOutDate, boolean isOwner, boolean active, List<FamilyMemberDto> familyMembers, List<VehicleDto> vehicles) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.buildingName = buildingName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.isOwner = isOwner;
        this.active = active;
        this.familyMembers = familyMembers;
        this.vehicles = vehicles;
    }

    public static ResidentDtoBuilder builder() { return new ResidentDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public LocalDate getMoveInDate() { return moveInDate; }
    public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
    public LocalDate getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(LocalDate moveOutDate) { this.moveOutDate = moveOutDate; }
    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean isOwner) { this.isOwner = isOwner; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<FamilyMemberDto> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<FamilyMemberDto> familyMembers) { this.familyMembers = familyMembers; }
    public List<VehicleDto> getVehicles() { return vehicles; }
    public void setVehicles(List<VehicleDto> vehicles) { this.vehicles = vehicles; }

    public static class ResidentDtoBuilder {
        private UUID id;
        private UUID userId;
        private String username;
        private UUID flatId;
        private String flatNumber;
        private String buildingName;
        private String firstName;
        private String lastName;
        private String fullName;
        private String email;
        private String phone;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private LocalDate moveInDate;
        private LocalDate moveOutDate;
        private boolean isOwner;
        private boolean active;
        private List<FamilyMemberDto> familyMembers;
        private List<VehicleDto> vehicles;

        public ResidentDtoBuilder id(UUID id) { this.id = id; return this; }
        public ResidentDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public ResidentDtoBuilder username(String username) { this.username = username; return this; }
        public ResidentDtoBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public ResidentDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public ResidentDtoBuilder buildingName(String buildingName) { this.buildingName = buildingName; return this; }
        public ResidentDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public ResidentDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public ResidentDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public ResidentDtoBuilder email(String email) { this.email = email; return this; }
        public ResidentDtoBuilder phone(String phone) { this.phone = phone; return this; }
        public ResidentDtoBuilder emergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; return this; }
        public ResidentDtoBuilder emergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; return this; }
        public ResidentDtoBuilder moveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; return this; }
        public ResidentDtoBuilder moveOutDate(LocalDate moveOutDate) { this.moveOutDate = moveOutDate; return this; }
        public ResidentDtoBuilder isOwner(boolean isOwner) { this.isOwner = isOwner; return this; }
        public ResidentDtoBuilder active(boolean active) { this.active = active; return this; }
        public ResidentDtoBuilder familyMembers(List<FamilyMemberDto> familyMembers) { this.familyMembers = familyMembers; return this; }
        public ResidentDtoBuilder vehicles(List<VehicleDto> vehicles) { this.vehicles = vehicles; return this; }

        public ResidentDto build() {
            return new ResidentDto(id, userId, username, flatId, flatNumber, buildingName, firstName, lastName, fullName, email, phone, emergencyContactName, emergencyContactPhone, moveInDate, moveOutDate, isOwner, active, familyMembers, vehicles);
        }
    }
}
