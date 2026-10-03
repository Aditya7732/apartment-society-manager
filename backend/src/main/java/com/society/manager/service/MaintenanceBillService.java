package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.bill.BatchBillGenerationRequest;
import com.society.manager.dto.bill.CreateBillRequest;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.enums.BillStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MaintenanceBillService {
    MaintenanceBillDto createBill(CreateBillRequest request);
    List<MaintenanceBillDto> generateBatchBills(BatchBillGenerationRequest request);
    MaintenanceBillDto getBillById(UUID id);
    PageResponse<MaintenanceBillDto> searchBills(UUID flatId, String billingPeriod, BillStatus status, String search, Pageable pageable);
    List<MaintenanceBillDto> getBillsByFlat(UUID flatId);
    MaintenanceBillDto cancelBill(UUID id);
    void updateOverdueBillsStatus();
    byte[] downloadBillPdf(UUID id);
}
