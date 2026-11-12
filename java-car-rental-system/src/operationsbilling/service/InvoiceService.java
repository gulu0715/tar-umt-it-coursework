package operationsbilling.service;

import fleetbooking.model.Booking;
import fleetbooking.service.BookingService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import operationsbilling.model.CarInspection;
import operationsbilling.model.Invoice;
import util.AppPaths;
import util.FileUtil;

public final class InvoiceService {
    private static Invoice[] invoices = new Invoice[20];
    private static int size = 0;
    private static boolean loaded = false;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private InvoiceService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        
        try {
            String[] lines = FileUtil.readAllLines(AppPaths.INVOICES_CSV_FILE);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("InvoiceId|")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 14) continue;
                
                LocalDateTime issuedAt = null;
                try { 
                    issuedAt = LocalDateTime.parse(p[p.length - 1], FMT); 
                } catch (Exception ignored) {}

                Invoice inv;
                
                // Check if we have 15 fields (including insurance) or 14 fields (without insurance)
                if (p.length >= 16) {
                    // Full version with insuranceCharge
                    inv = Invoice.restoreFromFile(
                            p[0], p[1], p[2], p[3],
                            parseDouble(p[4]), parseDouble(p[5]), parseDouble(p[6]), 
                            parseDouble(p[7]), parseDouble(p[8]), parseDouble(p[9]), parseDouble(p[10]),
                            p[11], p[12], p[13], issuedAt
                    );
                } else {
                    // Backward compatible version without insuranceCharge
                    inv = Invoice.restoreFromFile(
                            p[0], p[1], p[2], p[3],
                            parseDouble(p[4]), parseDouble(p[5]), parseDouble(p[6]), 
                            parseDouble(p[7]), parseDouble(p[8]), parseDouble(p[9]),
                            p[10], p[11], p[12], issuedAt
                    );
                }
                
                invoices = addInternal(invoices, inv);
                size++;
                Invoice.syncCounterFromInvoiceId(inv.getInvoiceId());
            }
        } catch (Exception e) {
            System.err.println("Error loading invoices: " + e.getMessage());
        }
        
        loaded = true;
    }

    public static void saveToFile(Invoice invoice) {
        if (invoice == null) return;
        FileUtil.appendLine(AppPaths.INVOICES_FILE, invoice.toFormattedString());
        
        boolean headerNeeded = true;
        String[] existingLines = FileUtil.readAllLines(AppPaths.INVOICES_CSV_FILE);
        if (existingLines.length > 0 && existingLines[0].startsWith("InvoiceId|")) {
            headerNeeded = false;
        }
        
        if (headerNeeded) {
            FileUtil.appendLine(AppPaths.INVOICES_CSV_FILE, "InvoiceId|BookingId|CustomerId|VehicleId|BaseRental|AddOn|Insurance|LateCharge|DamagePenalty|FuelPenalty|Total|InspectionId|InspectionRemarks|AddOnIds|IssuedAt");
        }
        FileUtil.appendLine(AppPaths.INVOICES_CSV_FILE, invoice.toCsvLine());
    }

    public static Invoice[] listAll() {
        Invoice[] out = new Invoice[size];
        for (int i = 0; i < size; i++) out[i] = invoices[i];
        return out;
    }

    public static Invoice getById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) {
            if (invoices[i].getInvoiceId().equalsIgnoreCase(target)) return invoices[i];
        }
        return null;
    }

    public static Invoice findByBookingId(String bookingId) {
        if (bookingId == null) return null;
        String target = bookingId.trim();
        for (int i = 0; i < size; i++) {
            if (invoices[i].getBookingId().equalsIgnoreCase(target)) return invoices[i];
        }
        return null;
    }

    public static Invoice generateInvoice(String bookingId) {
        Booking booking = BookingService.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return null;
        }
        if (!Booking.STATUS_RETURNED.equalsIgnoreCase(booking.getStatus())) {
            System.out.println("Invoice can only be generated for Returned bookings.");
            return null;
        }
        
        Invoice existing = findByBookingId(bookingId);
        if (existing != null) {
            System.out.println("Invoice already exists for this booking.");
            return existing;
        }

        CarInspection inspection = InspectionService.findLatestByBookingId(bookingId);
        double damagePenalty = InspectionService.damagePenalty(inspection);
        double fuelPenalty = InspectionService.fuelPenalty(inspection);

        Invoice invoice = new Invoice(booking, inspection, damagePenalty, fuelPenalty);
        invoices = addInternal(invoices, invoice);
        size++;
        saveToFile(invoice);
        return invoice;
    }

    public static void viewAllInvoices() {
        System.out.println("=".repeat(140));
        System.out.println(String.format("%-10s %-8s %-12s %-8s %-10s %-10s %-10s %-10s %-10s %-10s %-10s",
                "INV-ID", "BID", "CUSTOMER", "VEH", "BASE", "ADDON", "INS", "LATE", "DMG", "FUEL", "TOTAL"));
        System.out.println("=".repeat(140));
        if (size == 0) {
            System.out.println("No invoices generated yet.");
        } else {
            for (int i = 0; i < size; i++) System.out.println(invoices[i]);
        }
        System.out.println("=".repeat(140));
    }

    public static Invoice[] getAll() {
        Invoice[] out = new Invoice[size];
        for (int i = 0; i < size; i++) out[i] = invoices[i];
        return out;
    }

    private static double parseDouble(String s) { 
        try { 
            return Double.parseDouble(s.trim()); 
        } catch (Exception e) { 
            return 0; 
        } 
    }

    private static Invoice[] addInternal(Invoice[] arr, Invoice inv) {
        if (size >= arr.length) {
            Invoice[] next = new Invoice[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = inv;
        return arr;
    }
}