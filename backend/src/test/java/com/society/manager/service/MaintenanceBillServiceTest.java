package com.society.manager.service;

import com.society.manager.dto.bill.CreateBillRequest;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.entity.Flat;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.enums.BillStatus;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.FlatRepository;
import com.society.manager.repository.MaintenanceBillRepository;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.service.impl.MaintenanceBillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceBillServiceTest {

    @Mock
    private MaintenanceBillRepository billRepository;

    @Mock
    private FlatRepository flatRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private NotificationService notificationService;

    @Spy
    private EntityMapper entityMapper = new EntityMapper();

    @InjectMocks
    private MaintenanceBillServiceImpl billService;

    private Flat testFlat;
    private UUID flatId;

    @BeforeEach
    void setUp() {
        flatId = UUID.randomUUID();
        testFlat = Flat.builder()
                .id(flatId)
                .flatNumber("A-101")
                .floorNumber(1)
                .flatType("2BHK")
                .areaSqft(1200.0)
                .build();
    }

    @Test
    @DisplayName("Should create bill and calculate total correctly")
    void testCreateBill() {
        CreateBillRequest request = new CreateBillRequest();
        request.setFlatId(flatId);
        request.setBillingPeriod("2026-05");
        request.setBillDate(LocalDate.of(2026, 5, 1));
        request.setDueDate(LocalDate.of(2026, 5, 15));
        request.setBaseAmount(new BigDecimal("3500.00"));
        request.setParkingCharges(new BigDecimal("500.00"));
        request.setWaterCharges(new BigDecimal("300.00"));

        when(flatRepository.findById(flatId)).thenReturn(Optional.of(testFlat));
        when(billRepository.existsByFlatIdAndBillingPeriod(flatId, "2026-05")).thenReturn(false);
        when(billRepository.save(any(MaintenanceBill.class))).thenAnswer(invocation -> {
            MaintenanceBill bill = invocation.getArgument(0);
            if (bill.getId() == null) {
                bill.setId(UUID.randomUUID());
            }
            return bill;
        });

        MaintenanceBillDto billDto = billService.createBill(request);

        assertNotNull(billDto);
        assertEquals("BILL-202605-A101", billDto.getBillNumber());
        assertEquals(new BigDecimal("4300.00"), billDto.getTotalAmount());
        assertEquals(BillStatus.PENDING, billDto.getStatus());
    }
}
