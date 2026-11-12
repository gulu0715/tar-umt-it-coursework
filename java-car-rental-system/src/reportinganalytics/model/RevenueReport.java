package reportinganalytics.model;

import java.time.LocalDate;

public class RevenueReport extends Report {
    
    public RevenueReport(LocalDate startDate, LocalDate endDate, int bookingCount, double totalRevenue,
                        String topVehicleId, int topVehicleCount, double topVehicleRevenue,
                        String topCustomerId, double topCustomerRevenue) {
        super("Revenue", TYPE_REVENUE, startDate, endDate, bookingCount, totalRevenue,
                topVehicleId, topVehicleCount, topVehicleRevenue, topCustomerId, topCustomerRevenue);
    }
    
    @Override
    protected String getReportTitle() {
        return "REVENUE REPORT";
    }
    
    @Override
    protected String getSpecificContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Top Vehicle : %s (%d bookings, RM %.2f)%n", 
                getTopVehicleId(), getTopVehicleCount(), getTopVehicleRevenue()));
        sb.append(String.format("  Top Customer: %s (RM %.2f)%n", 
                getTopCustomerId(), getTopCustomerRevenue()));
        return sb.toString();
    }
    
    @Override
    public String toFormattedString() {
        // Call parent's implementation
        return super.toFormattedString();
    }
}