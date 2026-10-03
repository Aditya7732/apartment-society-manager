package com.society.manager.mapper;

import com.society.manager.dto.audit.AuditLogDto;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.dto.building.BuildingDto;
import com.society.manager.dto.complaint.ComplaintCommentDto;
import com.society.manager.dto.complaint.ComplaintDto;
import com.society.manager.dto.expense.SocietyExpenseDto;
import com.society.manager.dto.flat.FlatDto;
import com.society.manager.dto.notice.NoticeDto;
import com.society.manager.dto.notification.NotificationDto;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.dto.resident.FamilyMemberDto;
import com.society.manager.dto.resident.ResidentDto;
import com.society.manager.dto.resident.VehicleDto;
import com.society.manager.dto.staff.StaffDto;
import com.society.manager.dto.user.UserDto;
import com.society.manager.dto.visitor.VisitorDto;
import com.society.manager.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public BuildingDto toBuildingDto(Building building) {
        if (building == null) return null;
        return BuildingDto.builder()
                .id(building.getId())
                .name(building.getName())
                .code(building.getCode())
                .totalFloors(building.getTotalFloors())
                .totalFlats(building.getTotalFlats())
                .description(building.getDescription())
                .createdAt(building.getCreatedAt())
                .build();
    }

    public FlatDto toFlatDto(Flat flat) {
        if (flat == null) return null;
        return FlatDto.builder()
                .id(flat.getId())
                .buildingId(flat.getBuilding() != null ? flat.getBuilding().getId() : null)
                .buildingName(flat.getBuilding() != null ? flat.getBuilding().getName() : null)
                .buildingCode(flat.getBuilding() != null ? flat.getBuilding().getCode() : null)
                .flatNumber(flat.getFlatNumber())
                .floorNumber(flat.getFloorNumber())
                .flatType(flat.getFlatType())
                .areaSqft(flat.getAreaSqft())
                .occupancyStatus(flat.getOccupancyStatus())
                .ownerName(flat.getOwnerName())
                .ownerPhone(flat.getOwnerPhone())
                .ownerEmail(flat.getOwnerEmail())
                .parkingSlot(flat.getParkingSlot())
                .build();
    }

    public FamilyMemberDto toFamilyMemberDto(FamilyMember fm) {
        if (fm == null) return null;
        return FamilyMemberDto.builder()
                .id(fm.getId())
                .residentId(fm.getResident() != null ? fm.getResident().getId() : null)
                .fullName(fm.getFullName())
                .relation(fm.getRelation())
                .age(fm.getAge())
                .phoneNumber(fm.getPhoneNumber())
                .build();
    }

    public VehicleDto toVehicleDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        return VehicleDto.builder()
                .id(vehicle.getId())
                .residentId(vehicle.getResident() != null ? vehicle.getResident().getId() : null)
                .flatId(vehicle.getFlat() != null ? vehicle.getFlat().getId() : null)
                .flatNumber(vehicle.getFlat() != null ? vehicle.getFlat().getFlatNumber() : null)
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .parkingSlot(vehicle.getParkingSlot())
                .build();
    }

    public ResidentDto toResidentDto(Resident resident) {
        if (resident == null) return null;
        return ResidentDto.builder()
                .id(resident.getId())
                .userId(resident.getUser() != null ? resident.getUser().getId() : null)
                .username(resident.getUser() != null ? resident.getUser().getUsername() : null)
                .flatId(resident.getFlat() != null ? resident.getFlat().getId() : null)
                .flatNumber(resident.getFlat() != null ? resident.getFlat().getFlatNumber() : null)
                .buildingName(resident.getFlat() != null && resident.getFlat().getBuilding() != null ? resident.getFlat().getBuilding().getName() : null)
                .firstName(resident.getFirstName())
                .lastName(resident.getLastName())
                .fullName(resident.getFullName())
                .email(resident.getEmail())
                .phone(resident.getPhone())
                .emergencyContactName(resident.getEmergencyContactName())
                .emergencyContactPhone(resident.getEmergencyContactPhone())
                .moveInDate(resident.getMoveInDate())
                .moveOutDate(resident.getMoveOutDate())
                .isOwner(resident.isOwner())
                .active(resident.isActive())
                .familyMembers(resident.getFamilyMembers() != null ?
                        resident.getFamilyMembers().stream().map(this::toFamilyMemberDto).collect(Collectors.toList()) : Collections.emptyList())
                .vehicles(resident.getVehicles() != null ?
                        resident.getVehicles().stream().map(this::toVehicleDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public MaintenanceBillDto toMaintenanceBillDto(MaintenanceBill bill) {
        if (bill == null) return null;
        BigDecimal outstanding = bill.getTotalAmount().subtract(bill.getPaidAmount()).max(BigDecimal.ZERO);
        return MaintenanceBillDto.builder()
                .id(bill.getId())
                .flatId(bill.getFlat() != null ? bill.getFlat().getId() : null)
                .flatNumber(bill.getFlat() != null ? bill.getFlat().getFlatNumber() : null)
                .buildingName(bill.getFlat() != null && bill.getFlat().getBuilding() != null ? bill.getFlat().getBuilding().getName() : null)
                .billNumber(bill.getBillNumber())
                .billingPeriod(bill.getBillingPeriod())
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .baseAmount(bill.getBaseAmount())
                .parkingCharges(bill.getParkingCharges())
                .waterCharges(bill.getWaterCharges())
                .lateFee(bill.getLateFee())
                .otherCharges(bill.getOtherCharges())
                .discount(bill.getDiscount())
                .taxAmount(bill.getTaxAmount())
                .totalAmount(bill.getTotalAmount())
                .paidAmount(bill.getPaidAmount())
                .outstandingAmount(outstanding)
                .status(bill.getStatus())
                .notes(bill.getNotes())
                .build();
    }

    public PaymentDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        return PaymentDto.builder()
                .id(payment.getId())
                .billId(payment.getBill() != null ? payment.getBill().getId() : null)
                .billNumber(payment.getBill() != null ? payment.getBill().getBillNumber() : null)
                .billingPeriod(payment.getBill() != null ? payment.getBill().getBillingPeriod() : null)
                .residentId(payment.getResident() != null ? payment.getResident().getId() : null)
                .residentName(payment.getResident() != null ? payment.getResident().getFullName() : null)
                .flatNumber(payment.getBill() != null && payment.getBill().getFlat() != null ? payment.getBill().getFlat().getFlatNumber() : null)
                .receiptNumber(payment.getReceiptNumber())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .notes(payment.getNotes())
                .createdByName(payment.getCreatedBy() != null ? payment.getCreatedBy().getFullName() : "System")
                .build();
    }

    public ComplaintCommentDto toComplaintCommentDto(ComplaintComment comment) {
        if (comment == null) return null;
        return ComplaintCommentDto.builder()
                .id(comment.getId())
                .complaintId(comment.getComplaint() != null ? comment.getComplaint().getId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .userName(comment.getUser() != null ? comment.getUser().getFullName() : null)
                .userRole(comment.getUser() != null && !comment.getUser().getRoles().isEmpty() ?
                        comment.getUser().getRoles().iterator().next().getName().name() : "USER")
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public ComplaintDto toComplaintDto(Complaint complaint) {
        if (complaint == null) return null;
        return ComplaintDto.builder()
                .id(complaint.getId())
                .residentId(complaint.getResident() != null ? complaint.getResident().getId() : null)
                .residentName(complaint.getResident() != null ? complaint.getResident().getFullName() : null)
                .residentPhone(complaint.getResident() != null ? complaint.getResident().getPhone() : null)
                .flatId(complaint.getFlat() != null ? complaint.getFlat().getId() : null)
                .flatNumber(complaint.getFlat() != null ? complaint.getFlat().getFlatNumber() : null)
                .buildingName(complaint.getFlat() != null && complaint.getFlat().getBuilding() != null ? complaint.getFlat().getBuilding().getName() : null)
                .assignedStaffId(complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getId() : null)
                .assignedStaffName(complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getFullName() : null)
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .category(complaint.getCategory())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .resolution(complaint.getResolution())
                .closedDate(complaint.getClosedDate())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())
                .comments(complaint.getComments() != null ?
                        complaint.getComments().stream().map(this::toComplaintCommentDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public SocietyExpenseDto toSocietyExpenseDto(SocietyExpense expense) {
        if (expense == null) return null;
        return SocietyExpenseDto.builder()
                .id(expense.getId())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .vendorName(expense.getVendorName())
                .description(expense.getDescription())
                .paymentMethod(expense.getPaymentMethod())
                .invoiceNumber(expense.getInvoiceNumber())
                .attachmentPath(expense.getAttachmentPath())
                .createdByName(expense.getCreatedBy() != null ? expense.getCreatedBy().getFullName() : "Admin")
                .createdAt(expense.getCreatedAt())
                .build();
    }

    public NoticeDto toNoticeDto(Notice notice) {
        if (notice == null) return null;
        boolean isActive = notice.getExpiryDate() == null || notice.getExpiryDate().isAfter(LocalDateTime.now());
        return NoticeDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .priority(notice.getPriority())
                .audience(notice.getAudience())
                .publishDate(notice.getPublishDate())
                .expiryDate(notice.getExpiryDate())
                .attachmentPath(notice.getAttachmentPath())
                .createdByName(notice.getCreatedBy() != null ? notice.getCreatedBy().getFullName() : "Society Admin")
                .createdAt(notice.getCreatedAt())
                .active(isActive)
                .build();
    }

    public VisitorDto toVisitorDto(Visitor visitor) {
        if (visitor == null) return null;
        return VisitorDto.builder()
                .id(visitor.getId())
                .flatId(visitor.getFlat() != null ? visitor.getFlat().getId() : null)
                .flatNumber(visitor.getFlat() != null ? visitor.getFlat().getFlatNumber() : null)
                .buildingName(visitor.getFlat() != null && visitor.getFlat().getBuilding() != null ? visitor.getFlat().getBuilding().getName() : null)
                .residentId(visitor.getResident() != null ? visitor.getResident().getId() : null)
                .residentName(visitor.getResident() != null ? visitor.getResident().getFullName() : null)
                .visitorName(visitor.getVisitorName())
                .phoneNumber(visitor.getPhoneNumber())
                .purpose(visitor.getPurpose())
                .expectedArrival(visitor.getExpectedArrival())
                .expectedDeparture(visitor.getExpectedDeparture())
                .actualArrival(visitor.getActualArrival())
                .actualDeparture(visitor.getActualDeparture())
                .vehicleNumber(visitor.getVehicleNumber())
                .status(visitor.getStatus())
                .checkedInByName(visitor.getCheckedInBy() != null ? visitor.getCheckedInBy().getFullName() : null)
                .checkedOutByName(visitor.getCheckedOutBy() != null ? visitor.getCheckedOutBy().getFullName() : null)
                .build();
    }

    public StaffDto toStaffDto(Staff staff) {
        if (staff == null) return null;
        return StaffDto.builder()
                .id(staff.getId())
                .userId(staff.getUser() != null ? staff.getUser().getId() : null)
                .fullName(staff.getFullName())
                .phone(staff.getPhone())
                .role(staff.getRole())
                .joiningDate(staff.getJoiningDate())
                .salary(staff.getSalary())
                .active(staff.isActive())
                .emergencyContact(staff.getEmergencyContact())
                .build();
    }

    public AuditLogDto toAuditLogDto(AuditLog log) {
        if (log == null) return null;
        String roleStr = "SYSTEM";
        if (log.getUser() != null && !log.getUser().getRoles().isEmpty()) {
            roleStr = log.getUser().getRoles().iterator().next().getName().name();
        }
        return AuditLogDto.builder()
                .id(log.getId())
                .userName(log.getUser() != null ? log.getUser().getFullName() : "System")
                .userRole(roleStr)
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .ipAddress(log.getIpAddress())
                .previousValue(log.getPreviousValue())
                .newValue(log.getNewValue())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public NotificationDto toNotificationDto(Notification notification) {
        if (notification == null) return null;
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
