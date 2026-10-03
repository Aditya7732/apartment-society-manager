package com.society.manager.entity;

import com.society.manager.enums.StaffRole;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "staff")
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StaffRole role;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    public Staff() {}

    public Staff(UUID id, User user, String fullName, String phone, StaffRole role, LocalDate joiningDate, BigDecimal salary, boolean active, String emergencyContact) {
        this.id = id;
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.joiningDate = joiningDate;
        this.salary = salary;
        this.active = active;
        this.emergencyContact = emergencyContact;
    }

    public static StaffBuilder builder() { return new StaffBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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

    public static class StaffBuilder {
        private UUID id;
        private User user;
        private String fullName;
        private String phone;
        private StaffRole role;
        private LocalDate joiningDate;
        private BigDecimal salary;
        private boolean active = true;
        private String emergencyContact;

        public StaffBuilder id(UUID id) { this.id = id; return this; }
        public StaffBuilder user(User user) { this.user = user; return this; }
        public StaffBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public StaffBuilder phone(String phone) { this.phone = phone; return this; }
        public StaffBuilder role(StaffRole role) { this.role = role; return this; }
        public StaffBuilder joiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; return this; }
        public StaffBuilder salary(BigDecimal salary) { this.salary = salary; return this; }
        public StaffBuilder active(boolean active) { this.active = active; return this; }
        public StaffBuilder emergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; return this; }

        public Staff build() {
            return new Staff(id, user, fullName, phone, role, joiningDate, salary, active, emergencyContact);
        }
    }
}
