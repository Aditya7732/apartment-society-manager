package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.bill.BatchBillGenerationRequest;
import com.society.manager.dto.bill.CreateBillRequest;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.entity.Flat;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Resident;
import com.society.manager.enums.BillStatus;
import com.society.manager.enums.NotificationType;
import com.society.manager.exception.DuplicateResourceException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.FlatRepository;
import com.society.manager.repository.MaintenanceBillRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.MaintenanceBillService;
import com.society.manager.service.NotificationService;
import com.society.manager.security.SecurityUtils;
import com.society.manager.specification.BillSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceBillServiceImpl implements MaintenanceBillService {

    private final MaintenanceBillRepository billRepository;
    private final FlatRepository flatRepository;
    private final ResidentRepository residentRepository;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;
    private final com.society.manager.service.PdfGeneratorService pdfGeneratorService;

    @Override
    @Transactional
    public MaintenanceBillDto createBill(CreateBillRequest request) {
        Flat flat = flatRepository.findById(request.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + request.getFlatId()));

        if (billRepository.existsByFlatIdAndBillingPeriod(request.getFlatId(), request.getBillingPeriod())) {
            throw new DuplicateResourceException("Maintenance bill for flat " + flat.getFlatNumber() + " and period " + request.getBillingPeriod() + " already exists.");
        }

        String billNumber = generateUniqueBillNumber(request.getBillingPeriod(), flat.getFlatNumber());

        MaintenanceBill bill = MaintenanceBill.builder()
                .flat(flat)
                .billNumber(billNumber)
                .billingPeriod(request.getBillingPeriod())
                .billDate(request.getBillDate())
                .dueDate(request.getDueDate())
                .baseAmount(request.getBaseAmount())
                .parkingCharges(request.getParkingCharges() != null ? request.getParkingCharges() : BigDecimal.ZERO)
                .waterCharges(request.getWaterCharges() != null ? request.getWaterCharges() : BigDecimal.ZERO)
                .lateFee(request.getLateFee() != null ? request.getLateFee() : BigDecimal.ZERO)
                .otherCharges(request.getOtherCharges() != null ? request.getOtherCharges() : BigDecimal.ZERO)
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .status(BillStatus.PENDING)
                .notes(request.getNotes())
                .build();

        bill.calculateTotalAmount();
        MaintenanceBill saved = billRepository.save(bill);

        // Notify active resident if registered
        Optional<Resident> residentOpt = residentRepository.findByFlatIdAndActiveTrue(flat.getId());
        if (residentOpt.isPresent() && residentOpt.get().getUser() != null) {
            notificationService.createNotification(
                    residentOpt.get().getUser().getId(),
                    "New Maintenance Bill Generated",
                    "Maintenance bill " + saved.getBillNumber() + " for period " + saved.getBillingPeriod() + " (Amount: ₹" + saved.getTotalAmount() + ") is now due on " + saved.getDueDate() + ".",
                    NotificationType.BILL,
                    saved.getId() != null ? saved.getId().toString() : null
            );
        }

        auditLogService.logAction(null, "CREATE_MAINTENANCE_BILL", "MAINTENANCE_BILL", saved.getId() != null ? saved.getId().toString() : null, null, null, "Bill created: " + saved.getBillNumber());
        return mapToBillDtoWithResidentName(saved);
    }

    @Override
    @Transactional
    public List<MaintenanceBillDto> generateBatchBills(BatchBillGenerationRequest request) {
        List<Flat> targetFlats;
        if (request.getBuildingId() != null) {
            targetFlats = flatRepository.findByBuildingId(request.getBuildingId());
        } else {
            targetFlats = flatRepository.findAll();
        }

        List<MaintenanceBill> generatedBills = new ArrayList<>();
        for (Flat flat : targetFlats) {
            if (billRepository.existsByFlatIdAndBillingPeriod(flat.getId(), request.getBillingPeriod())) {
                log.info("Skipping flat {} - bill already exists for period {}", flat.getFlatNumber(), request.getBillingPeriod());
                continue;
            }

            String billNumber = generateUniqueBillNumber(request.getBillingPeriod(), flat.getFlatNumber());

            BigDecimal parking = flat.getParkingSlot() != null ? request.getDefaultParkingCharges() : BigDecimal.ZERO;

            MaintenanceBill bill = MaintenanceBill.builder()
                    .flat(flat)
                    .billNumber(billNumber)
                    .billingPeriod(request.getBillingPeriod())
                    .billDate(request.getBillDate())
                    .dueDate(request.getDueDate())
                    .baseAmount(request.getBaseAmount())
                    .parkingCharges(parking)
                    .waterCharges(request.getDefaultWaterCharges())
                    .lateFee(BigDecimal.ZERO)
                    .otherCharges(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .paidAmount(BigDecimal.ZERO)
                    .status(BillStatus.PENDING)
                    .notes(request.getNotes())
                    .build();

            bill.calculateTotalAmount();
            generatedBills.add(bill);
        }

        List<MaintenanceBill> savedBills = billRepository.saveAll(generatedBills);

        // Notify residents
        for (MaintenanceBill bill : savedBills) {
            Optional<Resident> residentOpt = residentRepository.findByFlatIdAndActiveTrue(bill.getFlat().getId());
            if (residentOpt.isPresent() && residentOpt.get().getUser() != null) {
                notificationService.createNotification(
                        residentOpt.get().getUser().getId(),
                        "Maintenance Bill Generated",
                        "Your maintenance bill " + bill.getBillNumber() + " of ₹" + bill.getTotalAmount() + " for period " + bill.getBillingPeriod() + " is ready.",
                        NotificationType.BILL,
                        bill.getId().toString()
                );
            }
        }

        auditLogService.logAction(null, "BATCH_GENERATE_BILLS", "MAINTENANCE_BILL", null, null, null, "Batch bills generated for " + savedBills.size() + " flats for period " + request.getBillingPeriod());

        return savedBills.stream().map(this::mapToBillDtoWithResidentName).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceBillDto getBillById(UUID id) {
        MaintenanceBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (bill.getFlat() == null || resident.getFlat() == null || !bill.getFlat().getId().equals(resident.getFlat().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another flat's maintenance bill.");
            }
        }

        return mapToBillDtoWithResidentName(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MaintenanceBillDto> searchBills(UUID flatId, String billingPeriod, BillStatus status, String search, Pageable pageable) {
        UUID effectiveFlatId = flatId;
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            effectiveFlatId = resident.getFlat() != null ? resident.getFlat().getId() : null;
        }

        Specification<MaintenanceBill> spec = BillSpecification.filterBills(effectiveFlatId, billingPeriod, status, search);
        Page<MaintenanceBill> page = billRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(this::mapToBillDtoWithResidentName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceBillDto> getBillsByFlat(UUID flatId) {
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (resident.getFlat() == null || !resident.getFlat().getId().equals(flatId)) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view bills for another flat.");
            }
        }

        return billRepository.findByFlatId(flatId)
                .stream()
                .map(this::mapToBillDtoWithResidentName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadBillPdf(UUID id) {
        MaintenanceBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));

        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (bill.getFlat() == null || resident.getFlat() == null || !bill.getFlat().getId().equals(resident.getFlat().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to download another flat's maintenance bill.");
            }
        }

        MaintenanceBillDto billDto = mapToBillDtoWithResidentName(bill);
        return pdfGeneratorService.generateMaintenanceBillPdf(billDto);
    }

    @Override
    @Transactional
    public MaintenanceBillDto cancelBill(UUID id) {
        MaintenanceBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));
        bill.setStatus(BillStatus.CANCELLED);
        MaintenanceBill updated = billRepository.save(bill);
        auditLogService.logAction(null, "CANCEL_BILL", "MAINTENANCE_BILL", id.toString(), null, null, "Bill cancelled: " + updated.getBillNumber());
        return mapToBillDtoWithResidentName(updated);
    }

    @Override
    @Transactional
    public void updateOverdueBillsStatus() {
        LocalDate today = LocalDate.now();
        List<MaintenanceBill> bills = billRepository.findAll();
        for (MaintenanceBill bill : bills) {
            if ((bill.getStatus() == BillStatus.PENDING || bill.getStatus() == BillStatus.PARTIALLY_PAID)
                    && bill.getDueDate().isBefore(today)) {
                bill.setStatus(BillStatus.OVERDUE);
                billRepository.save(bill);
            }
        }
    }

    private MaintenanceBillDto mapToBillDtoWithResidentName(MaintenanceBill bill) {
        MaintenanceBillDto dto = entityMapper.toMaintenanceBillDto(bill);
        if (bill.getFlat() != null) {
            Optional<Resident> residentOpt = residentRepository.findByFlatIdAndActiveTrue(bill.getFlat().getId());
            residentOpt.ifPresent(r -> dto.setResidentName(r.getFullName()));
        }
        return dto;
    }

    private String generateUniqueBillNumber(String billingPeriod, String flatNumber) {
        String base = "BILL-" + billingPeriod.replace("-", "") + "-" + flatNumber.replace("-", "");
        if (!billRepository.existsByBillNumber(base)) {
            return base;
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
