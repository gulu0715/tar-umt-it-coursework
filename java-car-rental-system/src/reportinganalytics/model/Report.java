package reportinganalytics.model;

import common.model.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Report extends Transaction {
    private static int counter = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Report type constants for polymorphism
    public static final int TYPE_REVENUE = 1;
    public static final int TYPE_VEHICLE = 2;
    public static final int TYPE_CUSTOMER = 3;

    private String reportId;
    private String reportType;
    private int reportTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    
    private int bookingCount;
    private double totalRevenue;
    private String topVehicleId;
    private int topVehicleCount;
    private double topVehicleRevenue;
    private String topCustomerId;
    private double topCustomerRevenue;

    // Full constructor
    public Report(String reportType, int reportTypeCode, LocalDate startDate, LocalDate endDate, 
                  int bookingCount, double totalRevenue,
                  String topVehicleId, int topVehicleCount, double topVehicleRevenue,
                  String topCustomerId, double topCustomerRevenue) {
        super(String.format("R%04d", counter++), LocalDateTime.now());
        this.reportId = transactionId;
        this.reportType = reportType;
        this.reportTypeCode = reportTypeCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = LocalDateTime.now();
        this.bookingCount = bookingCount;
        this.totalRevenue = totalRevenue;
        this.topVehicleId = topVehicleId;
        this.topVehicleCount = topVehicleCount;
        this.topVehicleRevenue = topVehicleRevenue;
        this.topCustomerId = topCustomerId;
        this.topCustomerRevenue = topCustomerRevenue;
    }

    // Constructor for Vehicle reports
    public Report(LocalDate startDate, LocalDate endDate, 
                  int bookingCount, double totalRevenue,
                  String topVehicleId, int topVehicleCount, double topVehicleRevenue) {
        this("Vehicle", TYPE_VEHICLE, startDate, endDate, bookingCount, totalRevenue,
             topVehicleId, topVehicleCount, topVehicleRevenue, "-", 0.0);
    }

    protected Report() {}

    public static Report restoreFromFile(String reportId, String reportType, LocalDate startDate, LocalDate endDate,
                                         LocalDateTime generatedAt, int bookingCount, double totalRevenue,
                                         String topVehicleId, int topVehicleCount, double topVehicleRevenue,
                                         String topCustomerId, double topCustomerRevenue) {
        Report r = new Report();
        r.transactionId = reportId;
        r.reportId = reportId;
        r.reportType = reportType;
        if ("Revenue".equalsIgnoreCase(reportType)) {
            r.reportTypeCode = TYPE_REVENUE;
        } else if ("Vehicle".equalsIgnoreCase(reportType)) {
            r.reportTypeCode = TYPE_VEHICLE;
        } else if ("Customer".equalsIgnoreCase(reportType)) {
            r.reportTypeCode = TYPE_CUSTOMER;
        }
        r.startDate = startDate;
        r.endDate = endDate;
        r.generatedAt = generatedAt != null ? generatedAt : LocalDateTime.now();
        r.bookingCount = bookingCount;
        r.totalRevenue = totalRevenue;
        r.topVehicleId = topVehicleId;
        r.topVehicleCount = topVehicleCount;
        r.topVehicleRevenue = topVehicleRevenue;
        r.topCustomerId = topCustomerId;
        r.topCustomerRevenue = topCustomerRevenue;
        r.createdAt = r.generatedAt;
        return r;
    }

    public static void advanceCounter(int nextValue) { 
        if (nextValue > counter) counter = nextValue; 
    }

    public static void syncCounterFromReportId(String reportId) {
        if (reportId == null) return;
        if (reportId.matches("R\\d{4}")) {
            try { 
                advanceCounter(Integer.parseInt(reportId.substring(1)) + 1); 
            } catch (Exception ignored) {}
        }
    }

    // Getters
    public String getReportId() { 
        return reportId; 
    }

    public String getReportType() { 
        return reportType; 
    }

    public int getReportTypeCode() { 
        return reportTypeCode; 
    }

    public LocalDate getStartDate() { 
        return startDate; 
    }

    public LocalDate getEndDate() { 
        return endDate; 
    }

    public LocalDateTime getGeneratedAt() { 
        return generatedAt; 
    }

    public int getBookingCount() { 
        return bookingCount; 
    }

    public double getTotalRevenue() { 
        return totalRevenue; 
    }

    public String getTopVehicleId() { 
        return topVehicleId; 
    }

    public int getTopVehicleCount() { 
        return topVehicleCount; 
    }

    public double getTopVehicleRevenue() { 
        return topVehicleRevenue; 
    }

    public String getTopCustomerId() { 
        return topCustomerId; 
    }

    public double getTopCustomerRevenue() { 
        return topCustomerRevenue; 
    }
    
    // Setters
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate; 
    }

    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
    }

    public void setBookingCount(int bookingCount) { 
        this.bookingCount = bookingCount; 
    }

    public void setTotalRevenue(double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }

    public void setTopVehicleId(String topVehicleId) { 
        this.topVehicleId = topVehicleId; 
    }

    public void setTopVehicleCount(int topVehicleCount) { 
        this.topVehicleCount = topVehicleCount; 
    }

    public void setTopVehicleRevenue(double topVehicleRevenue) { 
        this.topVehicleRevenue = topVehicleRevenue; 
    }

    public void setTopCustomerId(String topCustomerId) { 
        this.topCustomerId = topCustomerId; 
    }

    public void setTopCustomerRevenue(double topCustomerRevenue) { 
        this.topCustomerRevenue = topCustomerRevenue; 
    }

    public String toCsvLine() {
        return String.join("|",
                reportId, reportType, startDate.toString(), endDate.toString(),
                generatedAt.format(FMT), String.valueOf(bookingCount),
                String.format("%.2f", totalRevenue),
                topVehicleId == null ? "-" : topVehicleId,
                String.valueOf(topVehicleCount),
                String.format("%.2f", topVehicleRevenue),
                topCustomerId == null ? "-" : topCustomerId,
                String.format("%.2f", topCustomerRevenue));
    }

    public String toFormattedString() {
        String border = "=".repeat(60);
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n").append(border).append("\n");
        sb.append(String.format("  %-56s", getReportTitle())).append("\n");
        sb.append(border).append("\n");
        sb.append(String.format("  Report ID   : %s%n", reportId));
        sb.append(String.format("  Report Type : %s%n", reportType));
        sb.append(String.format("  Period      : %s to %s%n", startDate, endDate));
        sb.append(String.format("  Generated   : %s%n", generatedAt.format(FMT)));
        sb.append(border).append("\n");
        sb.append(String.format("  Bookings    : %d%n", bookingCount));
        sb.append(String.format("  Total Revenue: RM %.2f%n", totalRevenue));
        sb.append(border).append("\n");
        
        // Add specific content 
        sb.append(getSpecificContent());
        
        sb.append(border).append("\n");
        return sb.toString();
    }
    
    /**
     * Override in child classes to provide report-specific title
     */
    protected String getReportTitle() {
        return "REPORT";
    }
    
    /**
     * Override in child classes to provide report-specific content
     * Child classes should use super.getSpecificContent() if they want to include common content
     */
    protected String getSpecificContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Top Vehicle : %s (%d bookings, RM %.2f)%n", 
                topVehicleId, topVehicleCount, topVehicleRevenue));
        sb.append(String.format("  Top Customer: %s (RM %.2f)%n", 
                topCustomerId, topCustomerRevenue));
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Report report = (Report) obj;
        return bookingCount == report.bookingCount &&
               Double.compare(report.totalRevenue, totalRevenue) == 0 &&
               topVehicleCount == report.topVehicleCount &&
               Double.compare(report.topVehicleRevenue, topVehicleRevenue) == 0 &&
               Double.compare(report.topCustomerRevenue, topCustomerRevenue) == 0 &&
               reportTypeCode == report.reportTypeCode &&
               Objects.equals(reportId, report.reportId) &&
               Objects.equals(reportType, report.reportType) &&
               Objects.equals(startDate, report.startDate) &&
               Objects.equals(endDate, report.endDate) &&
               Objects.equals(generatedAt, report.generatedAt) &&
               Objects.equals(topVehicleId, report.topVehicleId) &&
               Objects.equals(topCustomerId, report.topCustomerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reportId, reportType, reportTypeCode, startDate, endDate, 
                           generatedAt, bookingCount, totalRevenue, topVehicleId, 
                           topVehicleCount, topVehicleRevenue, topCustomerId, topCustomerRevenue);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-10s %-12s %-12s %-20s %-8d RM%-10.2f %-8s %-8d RM%-10.2f %-12s RM%-10.2f",
                reportId, reportType, startDate, endDate, 
                generatedAt.format(FMT), bookingCount, totalRevenue,
                topVehicleId, topVehicleCount, topVehicleRevenue,
                topCustomerId, topCustomerRevenue);
    }
}