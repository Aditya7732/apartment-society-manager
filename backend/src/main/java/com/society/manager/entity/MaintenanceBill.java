package com.society.manager.entity;

import com.society.manager.enums.BillStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "maintenance_bills",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_flat_billing_period", columnNames = {"flat_id", "billing_period"})
    }
)
public class MaintenanceBill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @Column(name = "bill_number", nullable = false, unique = true, length = 50)
    private String billNumber;

    @Column(name = "billing_period", nullable = false, length = 20)
    private String billingPeriod;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "base_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount = BigDecimal.ZERO;

    @Column(name = "parking_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal parkingCharges = BigDecimal.ZERO;

    @Column(name = "water_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal waterCharges = BigDecimal.ZERO;

    @Column(name = "late_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "other_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(name = "discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillStatus status = BillStatus.GENERATED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public MaintenanceBill() {}

    public MaintenanceBill(UUID id, Flat flat, String billNumber, String billingPeriod, LocalDate billDate, LocalDate dueDate, BigDecimal baseAmount, BigDecimal parkingCharges, BigDecimal waterCharges, BigDecimal lateFee, BigDecimal otherCharges, BigDecimal discount, BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal paidAmount, BillStatus status, String notes) {
        this.id = id;
        this.flat = flat;
        this.billNumber = billNumber;
        this.billingPeriod = billingPeriod;
        this.billDate = billDate;
        this.dueDate = dueDate;
        if (baseAmount != null) this.baseAmount = baseAmount;
        if (parkingCharges != null) this.parkingCharges = parkingCharges;
        if (waterCharges != null) this.waterCharges = waterCharges;
        if (lateFee != null) this.lateFee = lateFee;
        if (otherCharges != null) this.otherCharges = otherCharges;
        if (discount != null) this.discount = discount;
        if (taxAmount != null) this.taxAmount = taxAmount;
        if (totalAmount != null) this.totalAmount = totalAmount;
        if (paidAmount != null) this.paidAmount = paidAmount;
        if (status != null) this.status = status;
        this.notes = notes;
    }

    public static MaintenanceBillBuilder builder() { return new MaintenanceBillBuilder(); }

    public void calculateTotalAmount() {
        BigDecimal subtotal = baseAmount
                .add(parkingCharges)
                .add(waterCharges)
                .add(otherCharges)
                .add(lateFee)
                .add(taxAmount);
        this.totalAmount = subtotal.subtract(discount).max(BigDecimal.ZERO);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Flat getFlat() { return flat; }
    public void setFlat(Flat flat) { this.flat = flat; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public BigDecimal getParkingCharges() { return parkingCharges; }
    public void setParkingCharges(BigDecimal parkingCharges) { this.parkingCharges = parkingCharges; }
    public BigDecimal getWaterCharges() { return waterCharges; }
    public void setWaterCharges(BigDecimal waterCharges) { this.waterCharges = waterCharges; }
    public BigDecimal getLateFee() { return lateFee; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class MaintenanceBillBuilder {
        private UUID id;
        private Flat flat;
        private String billNumber;
        private String billingPeriod;
        private LocalDate billDate;
        private LocalDate dueDate;
        private BigDecimal baseAmount = BigDecimal.ZERO;
        private BigDecimal parkingCharges = BigDecimal.ZERO;
        private BigDecimal waterCharges = BigDecimal.ZERO;
        private BigDecimal lateFee = BigDecimal.ZERO;
        private BigDecimal otherCharges = BigDecimal.ZERO;
        private BigDecimal discount = BigDecimal.ZERO;
        private BigDecimal taxAmount = BigDecimal.ZERO;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal paidAmount = BigDecimal.ZERO;
        private BillStatus status = BillStatus.GENERATED;
        private String notes;

        public MaintenanceBillBuilder id(UUID id) { this.id = id; return this; }
        public MaintenanceBillBuilder flat(Flat flat) { this.flat = flat; return this; }
        public MaintenanceBillBuilder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public MaintenanceBillBuilder billingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; return this; }
        public MaintenanceBillBuilder billDate(LocalDate billDate) { this.billDate = billDate; return this; }
        public MaintenanceBillBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public MaintenanceBillBuilder baseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; return this; }
        public MaintenanceBillBuilder parkingCharges(BigDecimal parkingCharges) { this.parkingCharges = parkingCharges; return this; }
        public MaintenanceBillBuilder waterCharges(BigDecimal waterCharges) { this.waterCharges = waterCharges; return this; }
        public MaintenanceBillBuilder lateFee(BigDecimal lateFee) { this.lateFee = lateFee; return this; }
        public MaintenanceBillBuilder otherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; return this; }
        public MaintenanceBillBuilder discount(BigDecimal discount) { this.discount = discount; return this; }
        public MaintenanceBillBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public MaintenanceBillBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public MaintenanceBillBuilder paidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; return this; }
        public MaintenanceBillBuilder status(BillStatus status) { this.status = status; return this; }
        public MaintenanceBillBuilder notes(String notes) { this.notes = notes; return this; }

        public MaintenanceBill build() {
            return new MaintenanceBill(id, flat, billNumber, billingPeriod, billDate, dueDate, baseAmount, parkingCharges, waterCharges, lateFee, otherCharges, discount, taxAmount, totalAmount, paidAmount, status, notes);
        }
    }
}
