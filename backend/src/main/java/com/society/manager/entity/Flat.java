package com.society.manager.entity;

import com.society.manager.enums.OccupancyStatus;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "flats",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_building_flat_number", columnNames = {"building_id", "flat_number"})
    }
)
public class Flat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "flat_number", nullable = false, length = 20)
    private String flatNumber;

    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber;

    @Column(name = "flat_type", nullable = false, length = 30)
    private String flatType;

    @Column(name = "area_sqft", nullable = false)
    private Double areaSqft;

    @Enumerated(EnumType.STRING)
    @Column(name = "occupancy_status", nullable = false, length = 30)
    private OccupancyStatus occupancyStatus = OccupancyStatus.VACANT;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "owner_phone", length = 20)
    private String ownerPhone;

    @Column(name = "owner_email", length = 100)
    private String ownerEmail;

    @Column(name = "parking_slot", length = 50)
    private String parkingSlot;

    public Flat() {}

    public Flat(UUID id, Building building, String flatNumber, Integer floorNumber, String flatType, Double areaSqft, OccupancyStatus occupancyStatus, String ownerName, String ownerPhone, String ownerEmail, String parkingSlot) {
        this.id = id;
        this.building = building;
        this.flatNumber = flatNumber;
        this.floorNumber = floorNumber;
        this.flatType = flatType;
        this.areaSqft = areaSqft;
        this.occupancyStatus = occupancyStatus != null ? occupancyStatus : OccupancyStatus.VACANT;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
        this.parkingSlot = parkingSlot;
    }

    public static FlatBuilder builder() { return new FlatBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
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

    public static class FlatBuilder {
        private UUID id;
        private Building building;
        private String flatNumber;
        private Integer floorNumber;
        private String flatType;
        private Double areaSqft;
        private OccupancyStatus occupancyStatus = OccupancyStatus.VACANT;
        private String ownerName;
        private String ownerPhone;
        private String ownerEmail;
        private String parkingSlot;

        public FlatBuilder id(UUID id) { this.id = id; return this; }
        public FlatBuilder building(Building building) { this.building = building; return this; }
        public FlatBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public FlatBuilder floorNumber(Integer floorNumber) { this.floorNumber = floorNumber; return this; }
        public FlatBuilder flatType(String flatType) { this.flatType = flatType; return this; }
        public FlatBuilder areaSqft(Double areaSqft) { this.areaSqft = areaSqft; return this; }
        public FlatBuilder occupancyStatus(OccupancyStatus occupancyStatus) { this.occupancyStatus = occupancyStatus; return this; }
        public FlatBuilder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public FlatBuilder ownerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; return this; }
        public FlatBuilder ownerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; return this; }
        public FlatBuilder parkingSlot(String parkingSlot) { this.parkingSlot = parkingSlot; return this; }

        public Flat build() {
            return new Flat(id, building, flatNumber, floorNumber, flatType, areaSqft, occupancyStatus, ownerName, ownerPhone, ownerEmail, parkingSlot);
        }
    }
}
