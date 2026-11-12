package reportinganalytics.model;

import java.time.LocalDate;

public class TopVehicleReport extends Report {
    
    public TopVehicleReport(LocalDate startDate, LocalDate endDate, int bookingCount, double totalRevenue,
                        String topVehicleId, int topVehicleCount, double topVehicleRevenue,
                        String topCustomerId, double topCustomerRevenue) {
        super("Vehicle", TYPE_VEHICLE, startDate, endDate, bookingCount, totalRevenue,
                topVehicleId, topVehicleCount, topVehicleRevenue, topCustomerId, topCustomerRevenue);
    }
    
    @Override
    protected String getReportTitle() {
        return "VEHICLE USAGE REPORT";
    }
    
    @Override
    protected String getSpecificContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  TOP VEHICLE : %s (%d bookings, RM %.2f)%n", 
                getTopVehicleId(), getTopVehicleCount(), getTopVehicleRevenue()));
        return sb.toString();
    }
    
    @Override
    public String toFormattedString() {
        return super.toFormattedString();
    }
}