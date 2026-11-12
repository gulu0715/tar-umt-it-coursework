package reportinganalytics.model;

import java.time.LocalDate;

public class CustomerSpendingReport extends Report {
    
    public CustomerSpendingReport(LocalDate startDate, LocalDate endDate, int bookingCount, double totalRevenue,
                        String topVehicleId, int topVehicleCount, double topVehicleRevenue,
                        String topCustomerId, double topCustomerRevenue) {
        super("Customer", TYPE_CUSTOMER, startDate, endDate, bookingCount, totalRevenue,
                topVehicleId, topVehicleCount, topVehicleRevenue, topCustomerId, topCustomerRevenue);
    }
    
    @Override
    protected String getReportTitle() {
        return "CUSTOMER SPENDING REPORT";
    }
    
    @Override
    protected String getSpecificContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  TOP CUSTOMER: %s (RM %.2f spent)%n", 
                getTopCustomerId(), getTopCustomerRevenue()));
        return sb.toString();
    }
    
    @Override
    public String toFormattedString() {
        return super.toFormattedString();
    }
}