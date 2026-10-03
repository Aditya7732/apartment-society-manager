package com.society.manager.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.society.manager.dto.bill.MaintenanceBillDto;
import com.society.manager.dto.payment.PaymentDto;
import com.society.manager.entity.Complaint;
import com.society.manager.entity.MaintenanceBill;
import com.society.manager.entity.Resident;
import com.society.manager.entity.SocietyExpense;
import com.society.manager.service.PdfGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);    // Royal Blue
    private static final Color SUCCESS_COLOR = new Color(22, 163, 74);    // Emerald
    private static final Color DANGER_COLOR = new Color(220, 38, 38);     // Crimson
    private static final Color TEXT_DARK = new Color(15, 23, 42);         // Slate 900
    private static final Color TEXT_MUTED = new Color(100, 116, 139);     // Slate 500
    private static final Color BORDER_COLOR = new Color(226, 232, 240);   // Slate 200
    private static final Color BG_LIGHT = new Color(248, 250, 252);       // Slate 50
    private static final Color BG_HEADER = new Color(241, 245, 249);      // Slate 100

    @Override
    public byte[] generatePaymentReceiptPdf(PaymentDto payment) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 45, 45, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "OFFICIAL PAYMENT RECEIPT", "Receipt No: " + safe(payment.getReceiptNumber()));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{38, 62});
            table.setSpacingBefore(10);
            table.setSpacingAfter(12);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_DARK);

            addRow(table, "Bill Reference", safe(payment.getBillNumber()), labelFont, valueFont);
            addRow(table, "Billing Period", safe(payment.getBillingPeriod()), labelFont, valueFont);
            addRow(table, "Flat / Unit", safe(payment.getFlatNumber()), labelFont, valueFont);
            addRow(table, "Resident Name", safe(payment.getResidentName()), labelFont, valueFont);
            addRow(table, "Payment Method", payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "N/A", labelFont, valueFont);
            addRow(table, "Transaction / UTR ID", safe(payment.getTransactionId()), labelFont, valueFont);
            String dt = payment.getPaymentDate() != null ? payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) : "N/A";
            addRow(table, "Payment Date & Time", dt, labelFont, valueFont);
            addRow(table, "Payment Status", payment.getStatus() != null ? payment.getStatus().name() : "SUCCESS", labelFont, valueFont);
            if (payment.getNotes() != null && !payment.getNotes().isBlank()) {
                addRow(table, "Remarks / Notes", payment.getNotes(), labelFont, valueFont);
            }
            document.add(table);

            // Amount summary
            PdfPTable amtTable = new PdfPTable(2);
            amtTable.setWidthPercentage(100);
            amtTable.setWidths(new float[]{50, 50});
            amtTable.setSpacingBefore(8);
            amtTable.setSpacingAfter(14);

            PdfPCell lbl = new PdfPCell(new Phrase("TOTAL AMOUNT RECEIVED", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_MUTED)));
            lbl.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            lbl.setVerticalAlignment(Element.ALIGN_MIDDLE);

            String amtStr = payment.getAmount() != null ? "INR " + payment.getAmount().setScale(2).toPlainString() : "INR 0.00";
            PdfPCell amtCell = new PdfPCell(new Phrase(amtStr, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, SUCCESS_COLOR)));
            amtCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            amtCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            amtTable.addCell(lbl);
            amtTable.addCell(amtCell);
            document.add(amtTable);

            addFooter(document, "Thank you for your prompt payment! Keep this receipt for your records.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for payment receipt: {}", payment.getReceiptNumber(), e);
            throw new RuntimeException("Could not generate payment receipt PDF", e);
        }
    }

    @Override
    public byte[] generateMaintenanceBillPdf(MaintenanceBillDto bill) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 45, 45, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "MAINTENANCE BILL INVOICE", "Bill No: " + safe(bill.getBillNumber()));

            // Meta table
            PdfPTable meta = new PdfPTable(4);
            meta.setWidthPercentage(100);
            meta.setWidths(new float[]{22, 28, 22, 28});
            meta.setSpacingBefore(10);
            meta.setSpacingAfter(14);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_DARK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

            addMetaCell(meta, "Flat / Unit:", labelFont);
            addMetaCell(meta, safe(bill.getFlatNumber()), valueFont);
            addMetaCell(meta, "Billing Period:", labelFont);
            addMetaCell(meta, safe(bill.getBillingPeriod()), valueFont);

            addMetaCell(meta, "Resident Name:", labelFont);
            addMetaCell(meta, safe(bill.getResidentName()), valueFont);
            addMetaCell(meta, "Bill Status:", labelFont);
            addMetaCell(meta, bill.getStatus() != null ? bill.getStatus().name() : "PENDING", valueFont);

            addMetaCell(meta, "Invoice Date:", labelFont);
            addMetaCell(meta, bill.getBillDate() != null ? bill.getBillDate().toString() : "N/A", valueFont);
            addMetaCell(meta, "Payment Due Date:", labelFont);
            addMetaCell(meta, bill.getDueDate() != null ? bill.getDueDate().toString() : "N/A", valueFont);

            document.add(meta);

            // Itemized Charges Breakdown Table
            PdfPTable itemTable = new PdfPTable(2);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[]{70, 30});
            itemTable.setSpacingBefore(8);
            itemTable.setSpacingAfter(12);

            addTableHeaderCell(itemTable, "Charge Description / Service Item");
            addTableHeaderCell(itemTable, "Amount (INR)");

            addItemChargeRow(itemTable, "Base Society Maintenance Fee", bill.getBaseAmount());
            if (bill.getWaterCharges() != null && bill.getWaterCharges().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Water Usage Charges", bill.getWaterCharges());
            }
            if (bill.getParkingCharges() != null && bill.getParkingCharges().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Dedicated Parking Slot Fee", bill.getParkingCharges());
            }
            if (bill.getOtherCharges() != null && bill.getOtherCharges().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Other Common Area / Amenities Fee", bill.getOtherCharges());
            }
            if (bill.getLateFee() != null && bill.getLateFee().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Late Payment Penalty / Fee", bill.getLateFee());
            }
            if (bill.getTaxAmount() != null && bill.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Applicable GST / Taxes", bill.getTaxAmount());
            }
            if (bill.getDiscount() != null && bill.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                addItemChargeRow(itemTable, "Early Payment Rebate / Discount (-)", bill.getDiscount().negate());
            }

            // Totals
            addTotalRow(itemTable, "Total Amount Due", bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO, false);
            addTotalRow(itemTable, "Amount Paid", bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO, false);
            BigDecimal netDue = (bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO)
                    .subtract(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
            addTotalRow(itemTable, "Balance Outstanding", netDue.max(BigDecimal.ZERO), true);

            document.add(itemTable);

            addFooter(document, "Please make payments before the due date to avoid late penalty charges.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for maintenance bill: {}", bill.getBillNumber(), e);
            throw new RuntimeException("Could not generate maintenance bill PDF", e);
        }
    }

    @Override
    public byte[] generateFinancialReportPdf(List<MaintenanceBill> bills, LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 35, 35, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            String periodStr = (startDate != null ? startDate.toString() : "All Time") + " to " + (endDate != null ? endDate.toString() : "Present");
            addHeader(document, "FINANCIAL MAINTENANCE COLLECTION REPORT", "Report Scope: " + periodStr);

            BigDecimal totalBilled = BigDecimal.ZERO;
            BigDecimal totalCollected = BigDecimal.ZERO;
            for (MaintenanceBill b : bills) {
                if (b.getTotalAmount() != null) totalBilled = totalBilled.add(b.getTotalAmount());
                if (b.getPaidAmount() != null) totalCollected = totalCollected.add(b.getPaidAmount());
            }
            BigDecimal totalOutstanding = totalBilled.subtract(totalCollected).max(BigDecimal.ZERO);

            // Summary KPIs
            PdfPTable kpi = new PdfPTable(3);
            kpi.setWidthPercentage(100);
            kpi.setSpacingBefore(8);
            kpi.setSpacingAfter(14);
            addKpiCell(kpi, "TOTAL BILLED", "INR " + totalBilled.setScale(2).toPlainString(), PRIMARY_COLOR);
            addKpiCell(kpi, "TOTAL COLLECTED", "INR " + totalCollected.setScale(2).toPlainString(), SUCCESS_COLOR);
            addKpiCell(kpi, "TOTAL OUTSTANDING", "INR " + totalOutstanding.setScale(2).toPlainString(), DANGER_COLOR);
            document.add(kpi);

            // Table of records
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{18, 10, 10, 12, 12, 12, 12, 14});
            table.setSpacingBefore(6);
            table.setSpacingAfter(10);

            addTableHeaderCell(table, "Bill Number");
            addTableHeaderCell(table, "Flat");
            addTableHeaderCell(table, "Period");
            addTableHeaderCell(table, "Bill Date");
            addTableHeaderCell(table, "Total (INR)");
            addTableHeaderCell(table, "Paid (INR)");
            addTableHeaderCell(table, "Due (INR)");
            addTableHeaderCell(table, "Status");

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
            for (MaintenanceBill b : bills) {
                addCell(table, b.getBillNumber(), cellFont);
                addCell(table, b.getFlat() != null ? b.getFlat().getFlatNumber() : "N/A", cellFont);
                addCell(table, b.getBillingPeriod(), cellFont);
                addCell(table, b.getBillDate() != null ? b.getBillDate().toString() : "N/A", cellFont);
                addCell(table, b.getTotalAmount() != null ? b.getTotalAmount().toPlainString() : "0.00", cellFont);
                addCell(table, b.getPaidAmount() != null ? b.getPaidAmount().toPlainString() : "0.00", cellFont);
                BigDecimal due = (b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                        .subtract(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO);
                addCell(table, due.toPlainString(), cellFont);
                addCell(table, b.getStatus() != null ? b.getStatus().name() : "N/A", cellFont);
            }

            document.add(table);
            addFooter(document, "Financial audit summary generated automatically from system ledger.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate financial report PDF", e);
            throw new RuntimeException("Could not generate financial report PDF", e);
        }
    }

    @Override
    public byte[] generateResidentOccupancyReportPdf(List<Resident> residents) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 35, 35, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "RESIDENTIAL OCCUPANCY & TENANCY DIRECTORY", "As of: " + LocalDate.now());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{20, 12, 18, 22, 14, 8, 6});
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableHeaderCell(table, "Resident Name");
            addTableHeaderCell(table, "Flat / Tower");
            addTableHeaderCell(table, "Phone Number");
            addTableHeaderCell(table, "Email Address");
            addTableHeaderCell(table, "Move-In Date");
            addTableHeaderCell(table, "Type");
            addTableHeaderCell(table, "Active");

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
            for (Resident r : residents) {
                addCell(table, r.getFullName(), cellFont);
                String flatStr = r.getFlat() != null ? r.getFlat().getFlatNumber() : "N/A";
                if (r.getFlat() != null && r.getFlat().getBuilding() != null) {
                    flatStr += " (" + r.getFlat().getBuilding().getName() + ")";
                }
                addCell(table, flatStr, cellFont);
                addCell(table, safe(r.getPhone()), cellFont);
                addCell(table, safe(r.getEmail()), cellFont);
                addCell(table, r.getMoveInDate() != null ? r.getMoveInDate().toString() : "N/A", cellFont);
                addCell(table, r.isOwner() ? "OWNER" : "TENANT", cellFont);
                addCell(table, r.isActive() ? "YES" : "NO", cellFont);
            }

            document.add(table);
            addFooter(document, "Confidential society resident registry for authorized committee usage.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate resident occupancy PDF", e);
            throw new RuntimeException("Could not generate resident occupancy report PDF", e);
        }
    }

    @Override
    public byte[] generateComplaintReportPdf(List<Complaint> complaints, LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 35, 35, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            String scope = (startDate != null ? startDate.toString() : "All") + " to " + (endDate != null ? endDate.toString() : "Present");
            addHeader(document, "HELPDESK & TICKET SLA RESOLUTION REPORT", "Report Scope: " + scope);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{14, 26, 12, 10, 14, 12, 12});
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableHeaderCell(table, "Ticket ID");
            addTableHeaderCell(table, "Title / Subject");
            addTableHeaderCell(table, "Category");
            addTableHeaderCell(table, "Priority");
            addTableHeaderCell(table, "Status");
            addTableHeaderCell(table, "Flat");
            addTableHeaderCell(table, "Assigned Staff");

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
            for (Complaint c : complaints) {
                addCell(table, c.getId() != null ? c.getId().toString().substring(0, 8) : "N/A", cellFont);
                addCell(table, c.getTitle(), cellFont);
                addCell(table, c.getCategory() != null ? c.getCategory().name() : "N/A", cellFont);
                addCell(table, c.getPriority() != null ? c.getPriority().name() : "N/A", cellFont);
                addCell(table, c.getStatus() != null ? c.getStatus().name() : "N/A", cellFont);
                addCell(table, c.getFlat() != null ? c.getFlat().getFlatNumber() : "N/A", cellFont);
                addCell(table, c.getAssignedStaff() != null ? c.getAssignedStaff().getFullName() : "Unassigned", cellFont);
            }

            document.add(table);
            addFooter(document, "Society helpdesk performance and service level agreement auditing log.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate complaints report PDF", e);
            throw new RuntimeException("Could not generate complaints report PDF", e);
        }
    }

    @Override
    public byte[] generateExpenseReportPdf(List<SocietyExpense> expenses, LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 35, 35, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            String scope = (startDate != null ? startDate.toString() : "All") + " to " + (endDate != null ? endDate.toString() : "Present");
            addHeader(document, "SOCIETY EXPENDITURE & VENDOR AUDIT REPORT", "Report Scope: " + scope);

            BigDecimal totalExp = BigDecimal.ZERO;
            for (SocietyExpense e : expenses) {
                if (e.getAmount() != null) totalExp = totalExp.add(e.getAmount());
            }

            PdfPTable kpi = new PdfPTable(1);
            kpi.setWidthPercentage(100);
            kpi.setSpacingBefore(8);
            kpi.setSpacingAfter(14);
            addKpiCell(kpi, "TOTAL RECORDED EXPENDITURE", "INR " + totalExp.setScale(2).toPlainString(), DANGER_COLOR);
            document.add(kpi);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{16, 20, 16, 20, 14, 14});
            table.setSpacingBefore(6);
            table.setSpacingAfter(10);

            addTableHeaderCell(table, "Category");
            addTableHeaderCell(table, "Description");
            addTableHeaderCell(table, "Amount (INR)");
            addTableHeaderCell(table, "Vendor");
            addTableHeaderCell(table, "Expense Date");
            addTableHeaderCell(table, "Payment Mode");

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
            for (SocietyExpense ex : expenses) {
                addCell(table, ex.getCategory() != null ? ex.getCategory().name() : "N/A", cellFont);
                addCell(table, safe(ex.getDescription()), cellFont);
                addCell(table, ex.getAmount() != null ? ex.getAmount().toPlainString() : "0.00", cellFont);
                addCell(table, safe(ex.getVendorName()), cellFont);
                addCell(table, ex.getExpenseDate() != null ? ex.getExpenseDate().toString() : "N/A", cellFont);
                addCell(table, ex.getPaymentMethod() != null ? ex.getPaymentMethod().name() : "N/A", cellFont);
            }

            document.add(table);
            addFooter(document, "Official society accounts ledger submitted for annual committee review.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate expense report PDF", e);
            throw new RuntimeException("Could not generate expense report PDF", e);
        }
    }

    @Override
    public byte[] generateDefaultersReportPdf(List<MaintenanceBill> defaulterBills) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 45, 45);
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "OVERDUE DEFAULTERS & PENDING DUES REPORT", "Generated as of: " + LocalDate.now());

            BigDecimal totalOverdue = BigDecimal.ZERO;
            for (MaintenanceBill b : defaulterBills) {
                BigDecimal pending = (b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                        .subtract(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO);
                totalOverdue = totalOverdue.add(pending.max(BigDecimal.ZERO));
            }

            PdfPTable kpi = new PdfPTable(2);
            kpi.setWidthPercentage(100);
            kpi.setWidths(new float[]{50, 50});
            kpi.setSpacingBefore(8);
            kpi.setSpacingAfter(14);
            addKpiCell(kpi, "DEFAULTER UNITS", String.valueOf(defaulterBills.size()), DANGER_COLOR);
            addKpiCell(kpi, "TOTAL OVERDUE DUES", "INR " + totalOverdue.setScale(2).toPlainString(), DANGER_COLOR);
            document.add(kpi);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{25, 20, 18, 18, 19});
            table.setSpacingBefore(6);
            table.setSpacingAfter(10);

            addTableHeaderCell(table, "Bill Number");
            addTableHeaderCell(table, "Flat / Unit");
            addTableHeaderCell(table, "Billing Period");
            addTableHeaderCell(table, "Due Date");
            addTableHeaderCell(table, "Pending (INR)");

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
            for (MaintenanceBill b : defaulterBills) {
                addCell(table, b.getBillNumber(), cellFont);
                addCell(table, b.getFlat() != null ? b.getFlat().getFlatNumber() : "N/A", cellFont);
                addCell(table, b.getBillingPeriod(), cellFont);
                addCell(table, b.getDueDate() != null ? b.getDueDate().toString() : "N/A", cellFont);
                BigDecimal pending = (b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                        .subtract(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO);
                addCell(table, pending.toPlainString(), cellFont);
            }

            document.add(table);
            addFooter(document, "Action item for committee notice delivery to outstanding flat owners.");
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate defaulters report PDF", e);
            throw new RuntimeException("Could not generate defaulters report PDF", e);
        }
    }

    // Helper methods for styling
    private void addHeader(Document doc, String title, String subtitle) throws DocumentException {
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
        Font subbrandFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, TEXT_DARK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED);

        Paragraph p1 = new Paragraph("APARTMENT SOCIETY MANAGER", brandFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);

        Paragraph p2 = new Paragraph("Residential Governance Platform  |  Official Records", subbrandFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setSpacingAfter(6);
        doc.add(p2);

        LineSeparator line = new LineSeparator(1.2f, 100, PRIMARY_COLOR, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));

        Paragraph pt = new Paragraph(title, titleFont);
        pt.setAlignment(Element.ALIGN_CENTER);
        pt.setSpacingBefore(10);
        pt.setSpacingAfter(3);
        doc.add(pt);

        if (subtitle != null) {
            Paragraph ps = new Paragraph(subtitle, subtitleFont);
            ps.setAlignment(Element.ALIGN_CENTER);
            ps.setSpacingAfter(10);
            doc.add(ps);
        }
    }

    private void addFooter(Document doc, String note) throws DocumentException {
        LineSeparator line = new LineSeparator(0.8f, 100, BORDER_COLOR, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));

        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SUCCESS_COLOR);
        Font muted = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED);

        Paragraph pNote = new Paragraph(note, noteFont);
        pNote.setAlignment(Element.ALIGN_CENTER);
        pNote.setSpacingBefore(10);
        pNote.setSpacingAfter(4);
        doc.add(pNote);

        String generated = "Generated on " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + " | System Document | Apartment Society Manager";
        Paragraph pGen = new Paragraph(generated, muted);
        pGen.setAlignment(Element.ALIGN_CENTER);
        doc.add(pGen);
    }

    private void addRow(PdfPTable table, String label, String value, Font lf, Font vf) {
        PdfPCell lc = new PdfPCell(new Phrase(label, lf));
        lc.setBorderColor(BORDER_COLOR);
        lc.setPadding(6);
        lc.setBackgroundColor(BG_LIGHT);
        PdfPCell vc = new PdfPCell(new Phrase(value, vf));
        vc.setBorderColor(BORDER_COLOR);
        vc.setPadding(6);
        table.addCell(lc);
        table.addCell(vc);
    }

    private void addMetaCell(PdfPTable table, String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        c.setPadding(4);
        table.addCell(c);
    }

    private void addTableHeaderCell(PdfPTable table, String headerText) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_DARK);
        PdfPCell cell = new PdfPCell(new Phrase(headerText, font));
        cell.setBackgroundColor(BG_HEADER);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addItemChargeRow(PdfPTable table, String desc, BigDecimal amt) {
        Font df = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);
        Font af = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_DARK);

        PdfPCell dc = new PdfPCell(new Phrase(desc, df));
        dc.setBorderColor(BORDER_COLOR);
        dc.setPadding(6);

        String amtStr = amt != null ? amt.setScale(2).toPlainString() : "0.00";
        PdfPCell ac = new PdfPCell(new Phrase(amtStr, af));
        ac.setBorderColor(BORDER_COLOR);
        ac.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ac.setPadding(6);

        table.addCell(dc);
        table.addCell(ac);
    }

    private void addTotalRow(PdfPTable table, String title, BigDecimal amount, boolean isHighlight) {
        Font tf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, isHighlight ? 11 : 9, isHighlight ? DANGER_COLOR : TEXT_DARK);
        PdfPCell tc = new PdfPCell(new Phrase(title, tf));
        tc.setBorderColor(BORDER_COLOR);
        tc.setBackgroundColor(isHighlight ? BG_LIGHT : Color.WHITE);
        tc.setPadding(6);

        String amtStr = "INR " + (amount != null ? amount.setScale(2).toPlainString() : "0.00");
        PdfPCell ac = new PdfPCell(new Phrase(amtStr, tf));
        ac.setBorderColor(BORDER_COLOR);
        ac.setBackgroundColor(isHighlight ? BG_LIGHT : Color.WHITE);
        ac.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ac.setPadding(6);

        table.addCell(tc);
        table.addCell(ac);
    }

    private void addKpiCell(PdfPTable table, String title, String value, Color color) {
        Font tf = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED);
        Font vf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color);

        PdfPCell c = new PdfPCell();
        c.setBorderColor(BORDER_COLOR);
        c.setBackgroundColor(BG_LIGHT);
        c.setPadding(8);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph pTitle = new Paragraph(title, tf);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        Paragraph pVal = new Paragraph(value, vf);
        pVal.setAlignment(Element.ALIGN_CENTER);

        c.addElement(pTitle);
        c.addElement(pVal);
        table.addCell(c);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(safe(text), font));
        c.setBorderColor(BORDER_COLOR);
        c.setPadding(5);
        table.addCell(c);
    }

    private String safe(String v) {
        return (v != null && !v.isBlank()) ? v : "N/A";
    }
}
