package com.society.manager.dto.flat;

import com.society.manager.enums.OccupancyStatus;
import java.util.UUID;

public class FlatDto {
    private UUID id;
    private UUID buildingId;
    private String buildingName;
    private String buildingCode;
    private String flatNumber;
    private Integer floorNumber;
    private String flatType;
    private Double areaSqft;
    private OccupancyStatus occupancyStatus;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String parkingSlot;
    private String currentResidentName;
    private String currentResidentPhone;

    public FlatDto() {}

    public FlatDto(UUID id, UUID buildingId, String buildingName, String buildingCode, String flatNumber, Integer floorNumber, String flatType, Double areaSqft, OccupancyStatus occupancyStatus, String ownerName, String ownerPhone, String ownerEmail, String parkingSlot, String currentResidentName, String currentResidentPhone) {
        this.id = id;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.buildingCode = buildingCode;
        this.flatNumber = flatNumber;
        this.floorNumber = floorNumber;
        this.flatType = flatType;
        this.areaSqft = areaSqft;
        this.occupancyStatus = occupancyStatus;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
        this.parkingSlot = parkingSlot;
        this.currentResidentName = currentResidentName;
        this.currentResidentPhone = currentResidentPhone;
    }

    public static FlatDtoBuilder builder() { return new FlatDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBuildingId() { return buildingId; }
    public void setBuildingId(UUID buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getBuildingCode() { return buildingCode; }
    public void setBuildingCode(String buildingCode) { this.buildingCode = buildingCode; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }
    public String getFlatType() { return flatType; }
    public void setFlatType(String flatType) { this.flatType = flatType; }
    public Double getAreaSqft() { return areaSqft; }
    public void setAreaSqft(Double areaSqft) { this.areaSqft = areaSqft; }
    public OccupancyStatus getOccupancyStatus() { return occupancyStatus; }
    public void setOccupancyStatus(OccupancyStatus occupancyStatus) { this.occupancyStatus = occupancyStatus; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; }
    public String getCurrentResidentName() { return currentResidentName; }
    public void setCurrentResidentName(String currentResidentName) { this.currentResidentName = currentResidentName; }
    public String getCurrentResidentPhone() { return currentResidentPhone; }
    public void setCurrentResidentPhone(String currentResidentPhone) { this.currentResidentPhone = currentResidentPhone; }

    public static class FlatDtoBuilder {
        private UUID id;
        private UUID buildingId;
        private String buildingName;
        private String buildingCode;
        private String flatNumber;
        private Integer floorNumber;
        private String flatType;
        private Double areaSqft;
        private OccupancyStatus occupancyStatus;
        private String ownerName;
        private String ownerPhone;
        private String ownerEmail;
        private String parkingSlot;
        private String currentResidentName;
        private String currentResidentPhone;

        public FlatDtoBuilder id(UUID id) { this.id = id; return this; }
        public FlatDtoBuilder buildingId(UUID buildingId) { this.buildingId = buildingId; return this; }
        public FlatDtoBuilder buildingName(String buildingName) { this.buildingName = buildingName; return this; }
        public FlatDtoBuilder buildingCode(String buildingCode) { this.buildingCode = buildingCode; return this; }
        public FlatDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public FlatDtoBuilder floorNumber(Integer floorNumber) { this.floorNumber = floorNumber; return this; }
        public FlatDtoBuilder flatType(String flatType) { this.flatType = flatType; return this; }
        public FlatDtoBuilder areaSqft(Double areaSqft) { this.areaSqft = areaSqft; return this; }
        public FlatDtoBuilder occupancyStatus(OccupancyStatus occupancyStatus) { this.occupancyStatus = occupancyStatus; return this; }
        public FlatDtoBuilder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public FlatDtoBuilder ownerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; return this; }
        public FlatDtoBuilder ownerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; return this; }
        public FlatDtoBuilder parkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; return this; }
        public FlatDtoBuilder currentResidentName(String currentResidentName) { this.currentResidentName = currentResidentName; return this; }
        public FlatDtoBuilder currentResidentPhone(String currentResidentPhone) { this.currentResidentPhone = currentResidentPhone; return this; }

        public FlatDto build() {
            return new FlatDto(id, buildingId, buildingName, buildingCode, flatNumber, floorNumber, flatType, areaSqft, occupancyStatus, ownerName, ownerPhone, ownerEmail, parkingSlot, currentResidentName, currentResidentPhone);
        }
    }
}
