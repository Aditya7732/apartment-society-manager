package com.society.manager.dto.flat;

import com.society.manager.enums.OccupancyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class CreateFlatRequest {
    @NotNull(message = "Building ID is required")
    private UUID buildingId;

    @NotBlank(message = "Flat number is required")
    private String flatNumber;

    @NotNull(message = "Floor number is required")
    @Min(value = 0, message = "Floor number cannot be negative")
    private Integer floorNumber;

    @NotBlank(message = "Flat type is required")
    private String flatType;

    @NotNull(message = "Area in sq.ft is required")
    @Positive(message = "Area must be positive")
    private Double areaSqft;

    private OccupancyStatus occupancyStatus = OccupancyStatus.VACANT;

    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String parkingSlot;

    public CreateFlatRequest() {}

    public UUID getBuildingId() { return buildingId; }
    public void setBuildingId(UUID buildingId) { this.buildingId = buildingId; }
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
}
