package com.society.manager.dto.resident;

import java.util.UUID;

public class VehicleDto {
    private UUID id;
    private UUID residentId;
    private UUID flatId;
    private String flatNumber;
    private String vehicleNumber;
    private String vehicleType;
    private String parkingSlot;

    public VehicleDto() {}

    public VehicleDto(UUID id, UUID residentId, UUID flatId, String flatNumber, String vehicleNumber, String vehicleType, String parkingSlot) {
        this.id = id;
        this.residentId = residentId;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.parkingSlot = parkingSlot;
    }

    public static VehicleDtoBuilder builder() { return new VehicleDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; }

    public static class VehicleDtoBuilder {
        private UUID id;
        private UUID residentId;
        private UUID flatId;
        private String flatNumber;
        private String vehicleNumber;
        private String vehicleType;
        private String parkingSlot;

        public VehicleDtoBuilder id(UUID id) { this.id = id; return this; }
        public VehicleDtoBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public VehicleDtoBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public VehicleDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public VehicleDtoBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public VehicleDtoBuilder vehicleType(String vehicleType) { this.vehicleType = vehicleType; return this; }
        public VehicleDtoBuilder parkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; return this; }

        public VehicleDto build() {
            return new VehicleDto(id, residentId, flatId, flatNumber, vehicleNumber, vehicleType, parkingSlot);
        }
    }
}
