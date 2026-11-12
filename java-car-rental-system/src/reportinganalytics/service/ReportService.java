package reportinganalytics.service;

import fleetbooking.model.Booking;
import fleetbooking.service.BookingService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import operationsbilling.model.Invoice;
import operationsbilling.service.InvoiceService;
import reportinganalytics.model.CustomerSpendingReport;
import reportinganalytics.model.Report;
import reportinganalytics.model.RevenueReport;
import reportinganalytics.model.TopVehicleReport;
import util.AppPaths;
import util.FileUtil;

public final class ReportService {
    private static Report[] reports = new Report[20];
    private static int size = 0;
    private static boolean loaded = false;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReportService() {}

    // ==================== DATA ACCESS METHODS  ====================
    
    private static Booking[] getAllBookings() {
        try {
            return BookingService.listAll();
        } catch (Exception e) {
            System.err.println("[ReportService] Error getting bookings: " + e.getMessage());
            return new Booking[0];
        }
    }
    
    private static Invoice[] getAllInvoices() {
        try {
            return InvoiceService.listAll();
        } catch (Exception e) {
            System.err.println("[ReportService] Error getting invoices: " + e.getMessage());
            return new Invoice[0];
        }
    }
    
    private static Booking[] getBookingsByReturnDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return new Booking[0];
        }
        
        try {
            Booking[] all = getAllBookings();
            // Count how many match first
            int matchCount = 0;
            for (int i = 0; i < all.length; i++) {
                Booking b = all[i];
                if (b != null && b.getReturnDate() != null) {
                    LocalDate returnDate = b.getReturnDate();
                    if (!returnDate.isBefore(start) && !returnDate.isAfter(end)) {
                        matchCount++;
                    }
                }
            }
            
            // Create array of matching size
            Booking[] filtered = new Booking[matchCount];
            int index = 0;
            for (int i = 0; i < all.length; i++) {
                Booking b = all[i];
                if (b != null && b.getReturnDate() != null) {
                    LocalDate returnDate = b.getReturnDate();
                    if (!returnDate.isBefore(start) && !returnDate.isAfter(end)) {
                        filtered[index++] = b;
                    }
                }
            }
            return filtered;
        } catch (Exception e) {
            System.err.println("[ReportService] Error filtering bookings: " + e.getMessage());
            return new Booking[0];
        }
    }
    
    private static Invoice[] getInvoicesByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return new Invoice[0];
        }
        
        try {
            Invoice[] all = getAllInvoices();
            // Count how many match first
            int matchCount = 0;
            for (int i = 0; i < all.length; i++) {
                Invoice inv = all[i];
                if (inv != null && inv.getIssuedAt() != null) {
                    LocalDate issuedDate = inv.getIssuedAt().toLocalDate();
                    if (!issuedDate.isBefore(start) && !issuedDate.isAfter(end)) {
                        matchCount++;
                    }
                }
            }
            
            // Create array of matching size
            Invoice[] filtered = new Invoice[matchCount];
            int index = 0;
            for (int i = 0; i < all.length; i++) {
                Invoice inv = all[i];
                if (inv != null && inv.getIssuedAt() != null) {
                    LocalDate issuedDate = inv.getIssuedAt().toLocalDate();
                    if (!issuedDate.isBefore(start) && !issuedDate.isAfter(end)) {
                        filtered[index++] = inv;
                    }
                }
            }
            return filtered;
        } catch (Exception e) {
            System.err.println("[ReportService] Error filtering invoices: " + e.getMessage());
            return new Invoice[0];
        }
    }
    
    private static double getBookingRevenue(Booking booking) {
        if (booking == null) return 0;
        try {
            return booking.getBaseCost() + booking.getAddOnCost() + 
                   booking.getInsuranceCost() + booking.getLateFee();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private static double getInvoiceTotalRevenue(Invoice invoice) {
        if (invoice == null) return 0;
        try {
            return invoice.getTotalAmount();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private static double calculateTotalRevenue(LocalDate start, LocalDate end) {
        Invoice[] invoices = getInvoicesByDateRange(start, end);
        double total = 0;
        for (int i = 0; i < invoices.length; i++) {
            if (invoices[i] != null) {
                total += getInvoiceTotalRevenue(invoices[i]);
            }
        }
        return total;
    }
    
    private static int getBookingCount(LocalDate start, LocalDate end) {
        Booking[] bookings = getBookingsByReturnDateRange(start, end);
        return bookings.length;
    }
    
    private static String[] getTopVehicleData(LocalDate start, LocalDate end) {
        Booking[] bookings = getBookingsByReturnDateRange(start, end);
        Invoice[] invoices = getInvoicesByDateRange(start, end);
        
        // First, find all unique vehicle IDs
        String[] vehicleIds = new String[bookings.length];
        int vehicleIdCount = 0;
        
        // Collect unique vehicle IDs
        for (int i = 0; i < bookings.length; i++) {
            Booking b = bookings[i];
            if (b != null) {
                String vid = b.getVehicleId();
                boolean found = false;
                for (int j = 0; j < vehicleIdCount; j++) {
                    if (vehicleIds[j] != null && vehicleIds[j].equals(vid)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    vehicleIds[vehicleIdCount++] = vid;
                }
            }
        }
        
        // Arrays to store counts and revenues
        int[] vehicleCounts = new int[vehicleIdCount];
        double[] vehicleRevenues = new double[vehicleIdCount];
        
        // Count bookings per vehicle
        for (int i = 0; i < bookings.length; i++) {
            Booking b = bookings[i];
            if (b != null) {
                String vid = b.getVehicleId();
                for (int j = 0; j < vehicleIdCount; j++) {
                    if (vehicleIds[j].equals(vid)) {
                        vehicleCounts[j]++;
                        break;
                    }
                }
            }
        }
        
        // Calculate revenue per vehicle from invoices
        for (int i = 0; i < invoices.length; i++) {
            Invoice inv = invoices[i];
            if (inv != null) {
                String vid = inv.getVehicleId();
                for (int j = 0; j < vehicleIdCount; j++) {
                    if (vehicleIds[j].equals(vid)) {
                        vehicleRevenues[j] += inv.getTotalAmount();
                        break;
                    }
                }
            }
        }
        
        // Find top vehicle
        String topVehicleId = "-";
        int topCount = 0;
        double topRevenue = 0;
        
        for (int j = 0; j < vehicleIdCount; j++) {
            if (vehicleCounts[j] > topCount) {
                topCount = vehicleCounts[j];
                topVehicleId = vehicleIds[j];
                topRevenue = vehicleRevenues[j];
            }
        }
        
        return new String[]{topVehicleId, String.valueOf(topCount), String.format("%.2f", topRevenue)};
    }

    private static String[] getTopCustomerData(LocalDate start, LocalDate end) {
        Invoice[] invoices = getInvoicesByDateRange(start, end);
        
        // First, find all unique customer IDs
        String[] customerIds = new String[invoices.length];
        int customerIdCount = 0;
        
        // Collect unique customer IDs
        for (int i = 0; i < invoices.length; i++) {
            Invoice inv = invoices[i];
            if (inv != null) {
                String cid = inv.getCustomerId();
                boolean found = false;
                for (int j = 0; j < customerIdCount; j++) {
                    if (customerIds[j] != null && customerIds[j].equals(cid)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    customerIds[customerIdCount++] = cid;
                }
            }
        }
        
        // Array to store spending
        double[] customerSpending = new double[customerIdCount];
        
        // Calculate spending per customer
        for (int i = 0; i < invoices.length; i++) {
            Invoice inv = invoices[i];
            if (inv != null) {
                String cid = inv.getCustomerId();
                for (int j = 0; j < customerIdCount; j++) {
                    if (customerIds[j].equals(cid)) {
                        customerSpending[j] += inv.getTotalAmount();
                        break;
                    }
                }
            }
        }
        
        // Find top customer
        String topCustomerId = "-";
        double topSpent = 0;
        
        for (int j = 0; j < customerIdCount; j++) {
            if (customerSpending[j] > topSpent) {
                topSpent = customerSpending[j];
                topCustomerId = customerIds[j];
            }
        }
        
        return new String[]{topCustomerId, String.format("%.2f", topSpent)};
    }
    
    private static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            System.err.println("Error: Start date cannot be null");
            return false;
        }
        if (endDate == null) {
            System.err.println("Error: End date cannot be null");
            return false;
        }
        if (endDate.isBefore(startDate)) {
            System.err.println("Error: End date cannot be before start date");
            return false;
        }
        return true;
    }

    // ==================== FILE I/O METHODS ====================

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        
        try {
            String[] lines = FileUtil.readAllLines(AppPaths.REPORTS_FILE);
            for (int lineIdx = 0; lineIdx < lines.length; lineIdx++) {
                String line = lines[lineIdx].trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 12) continue;
                
                try {
                    LocalDateTime generatedAt = null;
                    try {
                        generatedAt = LocalDateTime.parse(p[4], FMT);
                    } catch (DateTimeParseException e) {
                        generatedAt = LocalDateTime.now();
                    }
                    
                    Report r = Report.restoreFromFile(
                        p[0], p[1], LocalDate.parse(p[2]), LocalDate.parse(p[3]),
                        generatedAt, Integer.parseInt(p[5]), Double.parseDouble(p[6]),
                        p[7], Integer.parseInt(p[8]), Double.parseDouble(p[9]),
                        p[10], Double.parseDouble(p[11])
                    );
                    reports = addInternal(reports, r);
                    size++;
                    Report.syncCounterFromReportId(r.getReportId());
                } catch (Exception e) {
                    System.err.println("Error parsing report line: " + line);
                }
            }
        } catch (Exception e) {
            // File may not exist yet
        }
        
        loaded = true;
    }

    public static void saveToFile() {
        try {
            String[] lines = new String[size];
            for (int i = 0; i < size; i++) {
                lines[i] = reports[i].toCsvLine();
            }
            FileUtil.writeAllLines(AppPaths.REPORTS_FILE, lines);
        } catch (Exception e) {
            System.err.println("Error saving reports: " + e.getMessage());
        }
    }

    // ==================== REPORT MANAGEMENT METHODS ====================

    public static Report[] listAll() {
        Report[] out = new Report[size];
        for (int i = 0; i < size; i++) out[i] = reports[i];
        return out;
    }

    public static boolean isEmpty() { return size == 0; }

    public static Report getById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) {
            if (reports[i] != null && reports[i].getReportId().equalsIgnoreCase(target)) return reports[i];
        }
        return null;
    }

    // ==================== REPORT GENERATION METHODS ====================

    public static Report generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return null;
        }
        
        try {
            int bookingCount = getBookingCount(startDate, endDate);
            double totalRevenue = calculateTotalRevenue(startDate, endDate);
            String[] topVehicle = getTopVehicleData(startDate, endDate);
            String[] topCustomer = getTopCustomerData(startDate, endDate);
            
            RevenueReport report = new RevenueReport(
                startDate, endDate, bookingCount, totalRevenue,
                topVehicle[0], Integer.parseInt(topVehicle[1]), Double.parseDouble(topVehicle[2]),
                topCustomer[0], Double.parseDouble(topCustomer[1])
            );
            
            reports = addInternal(reports, report);
            size++;
            saveToFile();
            
            System.out.println(report.toFormattedString());
            return report;
            
        } catch (Exception e) {
            System.err.println("Error generating revenue report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Report generateVehicleReport(LocalDate startDate, LocalDate endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return null;
        }
        
        try {
            int bookingCount = getBookingCount(startDate, endDate);
            double totalRevenue = calculateTotalRevenue(startDate, endDate);
            String[] topVehicle = getTopVehicleData(startDate, endDate);
            
            TopVehicleReport report = new TopVehicleReport(
                startDate, endDate, bookingCount, totalRevenue,
                topVehicle[0], Integer.parseInt(topVehicle[1]), Double.parseDouble(topVehicle[2]),
                "-", 0.0
            );
            
            reports = addInternal(reports, report);
            size++;
            saveToFile();
            
            System.out.println(report.toFormattedString());
            return report;
            
        } catch (Exception e) {
            System.err.println("Error generating vehicle report: " + e.getMessage());
            return null;
        }
    }

    public static Report generateCustomerReport(LocalDate startDate, LocalDate endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return null;
        }
        
        try {
            int bookingCount = getBookingCount(startDate, endDate);
            double totalRevenue = calculateTotalRevenue(startDate, endDate);
            String[] topVehicle = getTopVehicleData(startDate, endDate);
            String[] topCustomer = getTopCustomerData(startDate, endDate);
            
            CustomerSpendingReport report = new CustomerSpendingReport(
                startDate, endDate, bookingCount, totalRevenue,
                topVehicle[0], Integer.parseInt(topVehicle[1]), 0.0,
                topCustomer[0], Double.parseDouble(topCustomer[1])
            );
            
            reports = addInternal(reports, report);
            size++;
            saveToFile();
            
            System.out.println(report.toFormattedString());
            return report;
            
        } catch (Exception e) {
            System.err.println("Error generating customer report: " + e.getMessage());
            return null;
        }
    }

    // ==================== DISPLAY METHODS ====================

    public static void viewAllReports() {
        if (size == 0) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("                    NO REPORTS GENERATED YET");
            System.out.println("=".repeat(70));
            System.out.println("Generate a report first (options 1-3) to see results here.");
            System.out.println("=".repeat(70) + "\n");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                           REPORTS");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < size; i++) {
            Report r = reports[i];
            if (r == null) continue;
            
            System.out.println();
            System.out.println("  Report #" + (i+1) + ": " + r.getReportId() + " (" + r.getReportType() + ")");
            System.out.println("  ┌─────────────────────────────────────────────────────────────────────────────┐");
            System.out.printf("  │ %-12s : %-60s │%n", "Period", r.getStartDate() + "  →  " + r.getEndDate());
            System.out.printf("  │ %-12s : %-60s │%n", "Generated", r.getGeneratedAt().format(FMT));
            System.out.println("  ├─────────────────────────────────────────────────────────────────────────────┤");
            System.out.printf("  │ %-12s : %-60d │%n", "Bookings", r.getBookingCount());
            System.out.printf("  │ %-12s : RM %-57.2f │%n", "Revenue", r.getTotalRevenue());
            System.out.println("  ├─────────────────────────────────────────────────────────────────────────────┤");
            System.out.printf("  │ %-12s : %-60s │%n", "Top Vehicle", r.getTopVehicleId() + " (" + r.getTopVehicleCount() + " bookings, RM " + String.format("%.2f", r.getTopVehicleRevenue()) + ")");
            System.out.printf("  │ %-12s : %-60s │%n", "Top Customer", r.getTopCustomerId() + " (RM " + String.format("%.2f", r.getTopCustomerRevenue()) + ")");
            System.out.println("  └─────────────────────────────────────────────────────────────────────────────┘");
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("  Total Reports: " + size);
        System.out.println("=".repeat(80) + "\n");
    }

    // ==================== UPDATE AND DELETE METHODS ====================

    public static boolean updateReport(String reportId, LocalDate newStartDate, LocalDate newEndDate) {
        if (reportId == null || reportId.trim().isEmpty()) {
            System.err.println("Error: Report ID cannot be null or empty");
            return false;
        }
        
        if (!isValidDateRange(newStartDate, newEndDate)) {
            return false;
        }
        
        try {
            Report existing = getById(reportId);
            if (existing == null) {
                System.err.println("Report not found: " + reportId);
                return false;
            }
            
            String reportType = existing.getReportType();
            LocalDate oldStart = existing.getStartDate();
            LocalDate oldEnd = existing.getEndDate();
            
            int originalBookingCount = existing.getBookingCount();
            double originalTotalRevenue = existing.getTotalRevenue();
            String originalTopVehicleId = existing.getTopVehicleId();
            int originalTopVehicleCount = existing.getTopVehicleCount();
            double originalTopVehicleRevenue = existing.getTopVehicleRevenue();
            String originalTopCustomerId = existing.getTopCustomerId();
            double originalTopCustomerRevenue = existing.getTopCustomerRevenue();
            
            // ONLY update the dates - KEEP ALL original data
            existing.setStartDate(newStartDate);
            existing.setEndDate(newEndDate);
            
            saveToFile();
            
            int boxWidth = 70;
            String topBorder = "┌" + "─".repeat(boxWidth) + "┐";
            String midBorder = "├" + "─".repeat(boxWidth) + "┤";
            String bottomBorder = "└" + "─".repeat(boxWidth) + "┘";
            
            System.out.println("\n✅ Report " + reportId + " updated successfully!");
            System.out.println(topBorder);
            System.out.printf("│ %-68s │%n", "REPORT UPDATE SUMMARY");
            System.out.println(midBorder);
            System.out.printf("│ %-20s : %-45s │%n", "Old Period", oldStart + "  →  " + oldEnd);
            System.out.printf("│ %-20s : %-45s │%n", "New Period", newStartDate + "  →  " + newEndDate);
            System.out.println(midBorder);
            System.out.printf("│ %-20s : %-45s │%n", "DATA PRESERVED", "(UNCHANGED)");
            System.out.println(midBorder);
            System.out.printf("│ %-20s : %-45d │%n", "Bookings", originalBookingCount);
            System.out.printf("│ %-20s : RM %-42.2f │%n", "Revenue", originalTotalRevenue);
            System.out.printf("│ %-20s : %-45s │%n", "Top Vehicle", originalTopVehicleId + " (" + originalTopVehicleCount + " bookings, RM " + String.format("%.2f", originalTopVehicleRevenue) + ")");
            if (!"Vehicle".equalsIgnoreCase(reportType)) {
                System.out.printf("│ %-20s : %-45s │%n", "Top Customer", originalTopCustomerId + " (RM " + String.format("%.2f", originalTopCustomerRevenue) + ")");
            }
            System.out.println(bottomBorder);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteReport(String reportId) {
        if (reportId == null || reportId.trim().isEmpty()) {
            System.err.println("Error: Report ID cannot be null or empty");
            return false;
        }
        
        try {
            String target = reportId.trim();
            int idx = -1;
            
            for (int i = 0; i < size; i++) {
                if (reports[i] != null && reports[i].getReportId().equalsIgnoreCase(target)) {
                    idx = i;
                    break;
                }
            }
            
            if (idx < 0) {
                System.err.println("Report not found: " + reportId);
                return false;
            }
            
            // Shift remaining reports to fill the gap
            for (int i = idx; i < size - 1; i++) {
                reports[i] = reports[i + 1];
            }
            reports[size - 1] = null;
            size--;
            saveToFile();
            
            System.out.println("✅ Report " + reportId + " deleted successfully.");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error deleting report: " + e.getMessage());
            return false;
        }
    }

    // ==================== EXPORT METHOD ====================

    public static boolean exportReportToCSV(String reportId) {
        if (reportId == null || reportId.trim().isEmpty()) {
            System.err.println("Error: Report ID cannot be null or empty");
            return false;
        }
        
        try {
            Report r = getById(reportId);
            if (r == null) {
                System.err.println("Report not found: " + reportId);
                return false;
            }
            
            String filename = "reports_data.csv";
            boolean fileExists = java.nio.file.Files.exists(java.nio.file.Path.of(filename));
            
            String csvLine = String.join("|",
                r.getReportId(),
                r.getReportType(),
                r.getStartDate().toString(),
                r.getEndDate().toString(),
                r.getGeneratedAt().format(FMT),
                String.valueOf(r.getBookingCount()),
                String.format("%.2f", r.getTotalRevenue()),
                r.getTopVehicleId(),
                String.valueOf(r.getTopVehicleCount()),
                String.format("%.2f", r.getTopVehicleRevenue()),
                r.getTopCustomerId(),
                String.format("%.2f", r.getTopCustomerRevenue())
            );
            
            boolean headerExists = false;
            if (fileExists) {
                String[] existingLines = FileUtil.readAllLines(filename);
                if (existingLines.length > 0 && existingLines[0].startsWith("ReportId|")) {
                    headerExists = true;
                }
            }
            
            if (!fileExists) {
                String header = "ReportId|ReportType|StartDate|EndDate|GeneratedAt|BookingCount|TotalRevenue|TopVehicleId|TopVehicleCount|TopVehicleRevenue|TopCustomerId|TopCustomerRevenue";
                FileUtil.writeAllLines(filename, new String[]{header});
            }
            
            FileUtil.appendLine(filename, csvLine);
            
            int boxWidth = 70;
            String topBorder = "┌" + "─".repeat(boxWidth) + "┐";
            String midBorder = "├" + "─".repeat(boxWidth) + "┤";
            String bottomBorder = "└" + "─".repeat(boxWidth) + "┘";
            
            System.out.println("\n✅ Report " + reportId + " exported successfully!");
            System.out.println(topBorder);
            System.out.printf("│ %-68s │%n", "EXPORT SUMMARY");
            System.out.println(midBorder);
            System.out.printf("│ %-20s : %-45s │%n", "File", filename);
            System.out.printf("│ %-20s : %-45s │%n", "Report ID", r.getReportId());
            System.out.printf("│ %-20s : %-45s │%n", "Report Type", r.getReportType());
            System.out.printf("│ %-20s : %-45s │%n", "Period", r.getStartDate() + " → " + r.getEndDate());
            System.out.println(midBorder);
            System.out.printf("│ %-20s : %-45s │%n", "Format", "Pipe-delimited CSV (|)");
            System.out.printf("│ %-20s : %-45s │%n", "Action", "Appended to existing file");
            System.out.println(bottomBorder);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== STATISTICS METHOD ====================

    public static void printReportStatistics() {
        if (size == 0) {
            System.out.println("No reports to display statistics for.");
            return;
        }
        
        double totalRevenue = 0;
        int totalBookings = 0;
        
        // Count report types using arrays
        String[] reportTypes = new String[size];
        int[] reportTypeCounts = new int[size];
        int typeCount = 0;
        
        for (int i = 0; i < size; i++) {
            Report r = reports[i];
            if (r == null) continue;
            
            totalRevenue += r.getTotalRevenue();
            totalBookings += r.getBookingCount();
            
            String type = r.getReportType();
            
            // Find or add type
            int foundIndex = -1;
            for (int j = 0; j < typeCount; j++) {
                if (reportTypes[j].equals(type)) {
                    foundIndex = j;
                    break;
                }
            }
            
            if (foundIndex >= 0) {
                reportTypeCounts[foundIndex]++;
            } else {
                reportTypes[typeCount] = type;
                reportTypeCounts[typeCount] = 1;
                typeCount++;
            }
        }
        
        String border = "=".repeat(50);
        System.out.println(border);
        System.out.println("        REPORT STATISTICS");
        System.out.println(border);
        System.out.println("Total Reports Generated: " + size);
        System.out.println("Total Bookings Across Reports: " + totalBookings);
        System.out.println("Total Revenue Across Reports: RM " + String.format("%.2f", totalRevenue));
        System.out.println(border);
        System.out.println("Reports by Type:");
        for (int i = 0; i < typeCount; i++) {
            System.out.println("  - " + reportTypes[i] + ": " + reportTypeCounts[i]);
        }
        System.out.println(border);
    }

    private static Report[] addInternal(Report[] arr, Report r) {
        if (size >= arr.length) {
            Report[] next = new Report[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = r;
        return arr;
    }
}