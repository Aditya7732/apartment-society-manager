package com.society.manager.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "residents")
public class Resident extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @Column(name = "is_owner", nullable = false)
    private boolean isOwner;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FamilyMember> familyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    public Resident() {}

    public Resident(UUID id, User user, Flat flat, String firstName, String lastName, String email, String phone, String emergencyContactName, String emergencyContactPhone, LocalDate moveInDate, LocalDate moveOutDate, boolean isOwner, boolean active, List<FamilyMember> familyMembers, List<Vehicle> vehicles) {
        this.id = id;
        this.user = user;
        this.flat = flat;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.isOwner = isOwner;
        this.active = active;
        if (familyMembers != null) this.familyMembers = familyMembers;
        if (vehicles != null) this.vehicles = vehicles;
    }

    public static ResidentBuilder builder() { return new ResidentBuilder(); }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Flat getFlat() { return flat; }
    public void setFlat(Flat flat) { this.flat = flat; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
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
    public List<FamilyMember> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<FamilyMember> familyMembers) { this.familyMembers = familyMembers; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public static class ResidentBuilder {
        private UUID id;
        private User user;
        private Flat flat;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private LocalDate moveInDate;
        private LocalDate moveOutDate;
        private boolean isOwner;
        private boolean active = true;
        private List<FamilyMember> familyMembers = new ArrayList<>();
        private List<Vehicle> vehicles = new ArrayList<>();

        public ResidentBuilder id(UUID id) { this.id = id; return this; }
        public ResidentBuilder user(User user) { this.user = user; return this; }
        public ResidentBuilder flat(Flat flat) { this.flat = flat; return this; }
        public ResidentBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public ResidentBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public ResidentBuilder email(String email) { this.email = email; return this; }
        public ResidentBuilder phone(String phone) { this.phone = phone; return this; }
        public ResidentBuilder emergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; return this; }
        public ResidentBuilder emergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; return this; }
        public ResidentBuilder moveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; return this; }
        public ResidentBuilder moveOutDate(LocalDate moveOutDate) { this.moveOutDate = moveOutDate; return this; }
        public ResidentBuilder isOwner(boolean isOwner) { this.isOwner = isOwner; return this; }
        public ResidentBuilder active(boolean active) { this.active = active; return this; }
        public ResidentBuilder familyMembers(List<FamilyMember> familyMembers) { this.familyMembers = familyMembers; return this; }
        public ResidentBuilder vehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; return this; }

        public Resident build() {
            return new Resident(id, user, flat, firstName, lastName, email, phone, emergencyContactName, emergencyContactPhone, moveInDate, moveOutDate, isOwner, active, familyMembers, vehicles);
        }
    }
}
