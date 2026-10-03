package com.society.manager.dto.visitor;

import com.society.manager.enums.VisitorStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class VisitorDto {
    private UUID id;
    private UUID flatId;
    private String flatNumber;
    private String buildingName;
    private UUID residentId;
    private String residentName;
    private String visitorName;
    private String phoneNumber;
    private String purpose;
    private LocalDateTime expectedArrival;
    private LocalDateTime expectedDeparture;
    private LocalDateTime actualArrival;
    private LocalDateTime actualDeparture;
    private String vehicleNumber;
    private VisitorStatus status;
    private String checkedInByName;
    private String checkedOutByName;

    public VisitorDto() {}

    public VisitorDto(UUID id, UUID flatId, String flatNumber, String buildingName, UUID residentId, String residentName, String visitorName, String phoneNumber, String purpose, LocalDateTime expectedArrival, LocalDateTime expectedDeparture, LocalDateTime actualArrival, LocalDateTime actualDeparture, String vehicleNumber, VisitorStatus status, String checkedInByName, String checkedOutByName) {
        this.id = id;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.buildingName = buildingName;
        this.residentId = residentId;
        this.residentName = residentName;
        this.visitorName = visitorName;
        this.phoneNumber = phoneNumber;
        this.purpose = purpose;
        this.expectedArrival = expectedArrival;
        this.expectedDeparture = expectedDeparture;
        this.actualArrival = actualArrival;
        this.actualDeparture = actualDeparture;
        this.vehicleNumber = vehicleNumber;
        this.status = status;
        this.checkedInByName = checkedInByName;
        this.checkedOutByName = checkedOutByName;
    }

    public static VisitorDtoBuilder builder() { return new VisitorDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public LocalDateTime getExpectedArrival() { return expectedArrival; }
    public void setExpectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; }
    public LocalDateTime getExpectedDeparture() { return expectedDeparture; }
    public void setExpectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; }
    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }
    public LocalDateTime getActualDeparture() { return actualDeparture; }
    public void setActualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public VisitorStatus getStatus() { return status; }
    public void setStatus(VisitorStatus status) { this.status = status; }
    public String getCheckedInByName() { return checkedInByName; }
    public void setCheckedInByName(String checkedInByName) { this.checkedInByName = checkedInByName; }
    public String getCheckedOutByName() { return checkedOutByName; }
    public void setCheckedOutByName(String checkedOutByName) { this.checkedOutByName = checkedOutByName; }

    public static class VisitorDtoBuilder {
        private UUID id;
        private UUID flatId;
        private String flatNumber;
        private String buildingName;
        private UUID residentId;
        private String residentName;
        private String visitorName;
        private String phoneNumber;
        private String purpose;
        private LocalDateTime expectedArrival;
        private LocalDateTime expectedDeparture;
        private LocalDateTime actualArrival;
        private LocalDateTime actualDeparture;
        private String vehicleNumber;
        private VisitorStatus status;
        private String checkedInByName;
        private String checkedOutByName;

        public VisitorDtoBuilder id(UUID id) { this.id = id; return this; }
        public VisitorDtoBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public VisitorDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public VisitorDtoBuilder buildingName(String buildingName) { this.buildingName = buildingName; return this; }
        public VisitorDtoBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public VisitorDtoBuilder residentName(String residentName) { this.residentName = residentName; return this; }
        public VisitorDtoBuilder visitorName(String visitorName) { this.visitorName = visitorName; return this; }
        public VisitorDtoBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public VisitorDtoBuilder purpose(String purpose) { this.purpose = purpose; return this; }
        public VisitorDtoBuilder expectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; return this; }
        public VisitorDtoBuilder expectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; return this; }
        public VisitorDtoBuilder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }
        public VisitorDtoBuilder actualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; return this; }
        public VisitorDtoBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public VisitorDtoBuilder status(VisitorStatus status) { this.status = status; return this; }
        public VisitorDtoBuilder checkedInByName(String checkedInByName) { this.checkedInByName = checkedInByName; return this; }
        public VisitorDtoBuilder checkedOutByName(String checkedOutByName) { this.checkedOutByName = checkedOutByName; return this; }

        public VisitorDto build() {
            return new VisitorDto(id, flatId, flatNumber, buildingName, residentId, residentName, visitorName, phoneNumber, purpose, expectedArrival, expectedDeparture, actualArrival, actualDeparture, vehicleNumber, status, checkedInInByName(checkedInByName), checkedOutByName);
        }

        private String checkedInInByName(String checkedInByName) { return checkedInByName; }
    }
}
