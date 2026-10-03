package com.society.manager.dto.bill;

import com.society.manager.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class MaintenanceBillDto {
    private UUID id;
    private UUID flatId;
    private String flatNumber;
    private String buildingName;
    private String residentName;
    private String billNumber;
    private String billingPeriod;
    private LocalDate billDate;
    private LocalDate dueDate;
    private BigDecimal baseAmount;
    private BigDecimal parkingCharges;
    private BigDecimal waterCharges;
    private BigDecimal lateFee;
    private BigDecimal otherCharges;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private BillStatus status;
    private String notes;

    public MaintenanceBillDto() {}

    public MaintenanceBillDto(UUID id, UUID flatId, String flatNumber, String buildingName, String residentName, String billNumber, String billingPeriod, LocalDate billDate, LocalDate dueDate, BigDecimal baseAmount, BigDecimal parkingCharges, BigDecimal waterCharges, BigDecimal lateFee, BigDecimal otherCharges, BigDecimal discount, BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal outstandingAmount, BillStatus status, String notes) {
        this.id = id;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.buildingName = buildingName;
        this.residentName = residentName;
        this.billNumber = billNumber;
        this.billingPeriod = billingPeriod;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.baseAmount = baseAmount;
        this.parkingCharges = parkingCharges;
        this.waterCharges = waterCharges;
        this.lateFee = lateFee;
        this.otherCharges = otherCharges;
        this.discount = discount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.outstandingAmount = outstandingAmount;
        this.status = status;
        this.notes = notes;
    }

    public static MaintenanceBillDtoBuilder builder() { return new MaintenanceBillDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
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
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class MaintenanceBillDtoBuilder {
        private UUID id;
        private UUID flatId;
        private String flatNumber;
        private String buildingName;
        private String residentName;
        private String billNumber;
        private String billingPeriod;
        private LocalDate billDate;
        private LocalDate dueDate;
        private BigDecimal baseAmount;
        private BigDecimal parkingCharges;
        private BigDecimal waterCharges;
        private BigDecimal lateFee;
        private BigDecimal otherCharges;
        private BigDecimal discount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal outstandingAmount;
        private BillStatus status;
        private String notes;

        public MaintenanceBillDtoBuilder id(UUID id) { this.id = id; return this; }
        public MaintenanceBillDtoBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public MaintenanceBillDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public MaintenanceBillDtoBuilder buildingName(String buildingName) { this.buildingName = buildingName; return this; }
        public MaintenanceBillDtoBuilder residentName(String residentName) { this.residentName = residentName; return this; }
        public MaintenanceBillDtoBuilder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public MaintenanceBillDtoBuilder billingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; return this; }
        public MaintenanceBillDtoBuilder billDate(LocalDate billDate) { this.billDate = billDate; return this; }
        public MaintenanceBillDtoBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public MaintenanceBillDtoBuilder baseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; return this; }
        public MaintenanceBillDtoBuilder parkingCharges(BigDecimal parkingCharges) { this.parkingCharges = parkingCharges; return this; }
        public MaintenanceBillDtoBuilder waterCharges(BigDecimal waterCharges) { this.waterCharges = waterCharges; return this; }
        public MaintenanceBillDtoBuilder lateFee(BigDecimal lateFee) { this.lateFee = lateFee; return this; }
        public MaintenanceBillDtoBuilder otherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; return this; }
        public MaintenanceBillDtoBuilder discount(BigDecimal discount) { this.discount = discount; return this; }
        public MaintenanceBillDtoBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public MaintenanceBillDtoBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public MaintenanceBillDtoBuilder paidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; return this; }
        public MaintenanceBillDtoBuilder outstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; return this; }
        public MaintenanceBillDtoBuilder status(BillStatus status) { this.status = status; return this; }
        public MaintenanceBillDtoBuilder notes(String notes) { this.notes = notes; return this; }

        public MaintenanceBillDto build() {
            return new MaintenanceBillDto(id, flatId, flatNumber, buildingName, residentName, billNumber, billingPeriod, billDate, dueDate, baseAmount, parkingCharges, waterCharges, lateFee, otherCharges, discount, taxAmount, totalAmount, paidAmount, outstandingAmount, status, notes);
        }
    }
}
