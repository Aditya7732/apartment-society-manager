package com.society.manager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 30)
    private String vehicleNumber;

    @Column(name = "vehicle_type", nullable = false, length = 20)
    private String vehicleType;

    @Column(name = "parking_slot", length = 50)
    private String parkingSlot;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(UUID id, Resident resident, Flat flat, String vehicleNumber, String vehicleType, String parkingSlot, LocalDateTime createdAt) {
        this.id = id;
        this.resident = resident;
        this.flat = flat;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.parkingSlot = parkingSlot;
        if (createdAt != null) this.createdAt = createdAt;
    }

    public static VehicleBuilder builder() { return new VehicleBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
    public Flat getFlat() { return flat; }
    public void setFlat(Flat flat) { this.flat = flat; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class VehicleBuilder {
        private UUID id;
        private Resident resident;
        private Flat flat;
        private String vehicleNumber;
        private String vehicleType;
        private String parkingSlot;
        private LocalDateTime createdAt = LocalDateTime.now();

        public VehicleBuilder id(UUID id) { this.id = id; return this; }
        public VehicleBuilder resident(Resident resident) { this.resident = resident; return this; }
        public VehicleBuilder flat(Flat flat) { this.flat = flat; return this; }
        public VehicleBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public VehicleBuilder vehicleType(String vehicleType) { this.vehicleType = vehicleType; return this; }
        public VehicleBuilder parkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; return this; }
        public VehicleBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Vehicle build() {
            return new Vehicle(id, resident, flat, vehicleNumber, vehicleType, parkingSlot, createdAt);
        }
    }
}
