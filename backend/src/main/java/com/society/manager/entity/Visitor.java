package com.society.manager.entity;

import com.society.manager.enums.VisitorStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visitors")
public class Visitor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @Column(name = "visitor_name", nullable = false, length = 100)
    private String visitorName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String purpose;

    @Column(name = "expected_arrival", nullable = false)
    private LocalDateTime expectedArrival;

    @Column(name = "expected_departure")
    private LocalDateTime expectedDeparture;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitorStatus status = VisitorStatus.EXPECTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by_id")
    private User checkedInBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_by_id")
    private User checkedOutBy;

    public Visitor() {}

    public Visitor(UUID id, Flat flat, Resident resident, String visitorName, String phoneNumber, String purpose, LocalDateTime expectedArrival, LocalDateTime expectedDeparture, LocalDateTime actualArrival, LocalDateTime actualDeparture, String vehicleNumber, VisitorStatus status, User checkedInBy, User checkedOutBy) {
        this.id = id;
        this.flat = flat;
        this.resident = resident;
        this.visitorName = visitorName;
        this.phoneNumber = phoneNumber;
        this.purpose = purpose;
        this.expectedArrival = expectedArrival;
        this.expectedDeparture = expectedDeparture;
        this.actualArrival = actualArrival;
        this.actualDeparture = actualDeparture;
        this.vehicleNumber = vehicleNumber;
        if (status != null) this.status = status;
        this.checkedInBy = checkedInBy;
        this.checkedOutBy = checkedOutBy;
    }

    public static VisitorBuilder builder() { return new VisitorBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Flat getFlat() { return flat; }
    public void setFlat(Flat flat) { this.flat = flat; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
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
    public User getCheckedInBy() { return checkedInBy; }
    public void setCheckedInBy(User checkedInBy) { this.checkedInBy = checkedInBy; }
    public User getCheckedOutBy() { return checkedOutBy; }
    public void setCheckedOutBy(User checkedOutBy) { this.checkedOutBy = checkedOutBy; }

    public static class VisitorBuilder {
        private UUID id;
        private Flat flat;
        private Resident resident;
        private String visitorName;
        private String phoneNumber;
        private String purpose;
        private LocalDateTime expectedArrival;
        private LocalDateTime expectedDeparture;
        private LocalDateTime actualArrival;
        private LocalDateTime actualDeparture;
        private String vehicleNumber;
        private VisitorStatus status = VisitorStatus.EXPECTED;
        private User checkedInBy;
        private User checkedOutBy;

        public VisitorBuilder id(UUID id) { this.id = id; return this; }
        public VisitorBuilder flat(Flat flat) { this.flat = flat; return this; }
        public VisitorBuilder resident(Resident resident) { this.resident = resident; return this; }
        public VisitorBuilder visitorName(String visitorName) { this.visitorName = visitorName; return this; }
        public VisitorBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public VisitorBuilder purpose(String purpose) { this.purpose = purpose; return this; }
        public VisitorBuilder expectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; return this; }
        public VisitorBuilder expectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; return this; }
        public VisitorBuilder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }
        public VisitorBuilder actualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; return this; }
        public VisitorBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public VisitorBuilder status(VisitorStatus status) { this.status = status; return this; }
        public VisitorBuilder checkedInBy(User checkedInBy) { this.checkedInBy = checkedInBy; return this; }
        public VisitorBuilder checkedOutBy(User checkedOutBy) { this.checkedOutBy = checkedOutBy; return this; }

        public Visitor build() {
            return new Visitor(id, flat, resident, visitorName, phoneNumber, purpose, expectedArrival, expectedDeparture, actualArrival, actualDeparture, vehicleNumber, status, checkedInBy, checkedOutBy);
        }
    }
}
