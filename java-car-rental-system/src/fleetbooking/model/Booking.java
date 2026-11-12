package fleetbooking.model;

import common.model.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class Booking extends Transaction {
    private static int counter = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private String bookingId;
    private String customerId;
    private String vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double dailyRate;
    private long bookedDays;
    private double baseCost;
    private double addOnCost;
    private double insuranceCost;
    private double lateFee;
    private double totalCost;
    private String status;
    private LocalDate checkoutDate;
    private LocalDate returnDate;
    private String[] addOnIds = new String[0];

    public static final String STATUS_PENDING_APPROVAL = "PendingApproval";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_PENDING_CHECKOUT = "PendingCheckout";
    public static final String STATUS_READY_FOR_CHECKOUT = "ReadyForCheckout";
    public static final String STATUS_CHECKED_OUT = "CheckedOut";
    public static final String STATUS_PENDING_RETURN = "PendingReturn";
    public static final String STATUS_RETURNED = "Returned";
    public static final String STATUS_CANCELLED = "Cancelled";

    // Contructor
    public Booking(String customerId, String vehicleId, LocalDate startDate, LocalDate endDate,
                   double dailyRate, String[] addOnIds, double addOnCost) {
        this(customerId, vehicleId, startDate, endDate, dailyRate, addOnIds, addOnCost, 0.0);
    }

    // Overloaded Contructor
    public Booking(String customerId, String vehicleId, LocalDate startDate, LocalDate endDate,
                   double dailyRate, String[] addOnIds, double addOnCost, double insuranceCost) {
        super(String.format("B%04d", counter++), LocalDateTime.now());
        this.bookingId = transactionId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.bookedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (this.bookedDays < 1) this.bookedDays = 1;
        this.baseCost = this.bookedDays * dailyRate;
        this.addOnIds = copyIds(addOnIds);
        this.addOnCost = Math.max(0, addOnCost);
        this.insuranceCost = Math.max(0, insuranceCost);
        this.lateFee = 0;
        recalcTotal();
        this.status = STATUS_PENDING_APPROVAL;
    }

    // Empty Contructor
    private Booking() {}

    // Static Contructor
    public static Booking restoreFromFile(String bookingId, String customerId, String vehicleId,
                                          LocalDate startDate, LocalDate endDate, double dailyRate,
                                          long bookedDays, double baseCost, double addOnCost, double insuranceCost,
                                          double lateFee, double totalCost, String status, LocalDate checkoutDate,
                                          LocalDate returnDate, String[] addOnIds) {
        Booking b = new Booking();
        b.transactionId = bookingId;
        b.createdAt = LocalDateTime.now();
        b.bookingId = bookingId;
        b.customerId = customerId;
        b.vehicleId = vehicleId;
        b.startDate = startDate;
        b.endDate = endDate;
        b.dailyRate = dailyRate;
        b.bookedDays = bookedDays > 0 ? bookedDays : (ChronoUnit.DAYS.between(startDate, endDate) + 1);
        if (b.bookedDays < 1) b.bookedDays = 1;
        b.baseCost = baseCost > 0 ? baseCost : b.bookedDays * dailyRate;
        b.addOnCost = Math.max(0, addOnCost);
        b.insuranceCost = Math.max(0, insuranceCost);
        b.lateFee = Math.max(0, lateFee);
        b.totalCost = b.baseCost + b.addOnCost + b.insuranceCost + b.lateFee;
        b.status = (status == null || status.isBlank()) ? STATUS_PENDING_APPROVAL : status;
        b.checkoutDate = checkoutDate;
        b.returnDate = returnDate;
        b.addOnIds = copyIds(addOnIds);
        return b;
    }

    // Static Contructor
    public static Booking restoreFromFile(String bookingId, String customerId, String vehicleId,
                                          LocalDate startDate, LocalDate endDate, double dailyRate,
                                          long bookedDays, double baseCost, double addOnCost, double lateFee,
                                          double totalCost, String status, LocalDate checkoutDate,
                                          LocalDate returnDate, String[] addOnIds) {
        return restoreFromFile(bookingId, customerId, vehicleId, startDate, endDate, dailyRate, bookedDays,
                baseCost, addOnCost, 0.0, lateFee, totalCost, status, checkoutDate, returnDate, addOnIds);
    }

    public static void advanceCounter(int nextValue) {
        if (nextValue > counter) counter = nextValue;
    }

    public static void syncCounterFromBookingId(String bookingId) {
        if (bookingId == null) return;
        bookingId = bookingId.trim();
        if (bookingId.matches("B\\d{4}")) {
            try {
                int n = Integer.parseInt(bookingId.substring(1));
                advanceCounter(n + 1);
            } catch (Exception ignored) {}
        }
    }

    // Getters
    public String getBookingId(){ 
        return bookingId; 
    }

    public String getCustomerId(){ 
        return customerId; 
    }

    public String getVehicleId(){ 
        return vehicleId; 
    }

    public LocalDate getStartDate(){ 
        return startDate; 
    }

    public LocalDate getEndDate(){ 
        return endDate; 
    }

    public double getDailyRate(){ 
        return dailyRate; 
    }

    public long getBookedDays(){ 
        return bookedDays; 
    }

    public double getBaseCost(){ 
        return baseCost; 
    }

    public double getAddOnCost(){ 
        return addOnCost; 
    }

    public double getInsuranceCost(){ 
        return insuranceCost; 
    }

    public double getLateFee(){ 
        return lateFee; 
    }

    public double getTotalCost(){ 
        return totalCost; 
    }

    public String getStatus(){ 
        return status; 
    }

    public LocalDate getCheckoutDate(){ 
        return checkoutDate; 
    }

    public LocalDate getReturnDate(){ 
        return returnDate; 
    }

    public String[] getAddOnIds(){ 
        return addOnIds == null ? new String[0] : Arrays.copyOf(addOnIds, addOnIds.length); 
    }

    // Setters
    public void setAddOnCost(double addOnCost) {
        this.addOnCost = Math.max(0, addOnCost);
        recalcTotal();
    }

    public void setInsuranceCost(double insuranceCost) {
        this.insuranceCost = Math.max(0, insuranceCost);
        recalcTotal();
    }

    public void setLateFee(double lateFee) {
        this.lateFee = Math.max(0, lateFee);
        recalcTotal();
    }

    private void recalcTotal() {
        this.totalCost = this.baseCost + this.addOnCost + this.insuranceCost + this.lateFee;
    }

    public boolean approve() {
        if (STATUS_PENDING_APPROVAL.equalsIgnoreCase(status)) {
            status = STATUS_APPROVED;
            return true;
        }
        return false;
    }

    public boolean reject() {
        if (STATUS_PENDING_APPROVAL.equalsIgnoreCase(status)) {
            status = STATUS_CANCELLED;
            return true;
        }
        return false;
    }

    public boolean requestCheckout() {
        if (STATUS_APPROVED.equalsIgnoreCase(status)) {
            status = STATUS_PENDING_CHECKOUT;
            return true;
        }
        return false;
    }

    public boolean confirmCheckout() {
        if (STATUS_PENDING_CHECKOUT.equalsIgnoreCase(status)) {
            status = STATUS_READY_FOR_CHECKOUT;
            return true;
        }
        return false;
    }

    public boolean pickupVehicle(LocalDate pickupDate) {
        if (!STATUS_READY_FOR_CHECKOUT.equalsIgnoreCase(status)) return false;
        this.checkoutDate = pickupDate;
        this.status = STATUS_CHECKED_OUT;
        return true;
    }

    public boolean requestReturn() {
        if (STATUS_CHECKED_OUT.equalsIgnoreCase(status)) {
            status = STATUS_PENDING_RETURN;
            return true;
        }
        return false;
    }

    public double confirmReturn(LocalDate actualReturnDate) {
        if (!STATUS_PENDING_RETURN.equalsIgnoreCase(status)) {
            return Double.NaN;
        }
        if (checkoutDate != null && actualReturnDate.isBefore(checkoutDate)) {
            return Double.NaN;
        }
        this.returnDate = actualReturnDate;
        long lateDays = ChronoUnit.DAYS.between(endDate, actualReturnDate);
        if (lateDays < 0) lateDays = 0;
        this.lateFee = lateDays * dailyRate;
        this.status = STATUS_RETURNED;
        recalcTotal();
        return lateFee;
    }

    public boolean cancel() {
        if (STATUS_PENDING_APPROVAL.equalsIgnoreCase(status)
                || STATUS_APPROVED.equalsIgnoreCase(status)
                || STATUS_PENDING_CHECKOUT.equalsIgnoreCase(status)) {
            status = STATUS_CANCELLED;
            return true;
        }
        return false;
    }

    public String toCsvLine() {
        String addOns = addOnIds == null || addOnIds.length == 0 ? "" : String.join(",", addOnIds);
        return String.join("|",
                bookingId,
                customerId,
                vehicleId,
                startDate.format(FMT),
                endDate.format(FMT),
                String.valueOf(dailyRate),
                String.valueOf(bookedDays),
                String.valueOf(baseCost),
                String.valueOf(addOnCost),
                String.valueOf(lateFee),
                String.valueOf(totalCost),
                status,
                checkoutDate == null ? "" : checkoutDate.format(FMT),
                returnDate == null ? "" : returnDate.format(FMT),
                addOns,
                String.valueOf(insuranceCost));
    }

    public static String[] parseAddOnIds(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new String[0];
        String[] raw = csv.split(",");
        String[] out = new String[0];
        for (String s : raw) {
            if (s != null) {
                String t = s.trim();
                if (!t.isEmpty()) out = append(out, t);
            }
        }
        return out;
    }

    @Override
    public String toString() {
        String addons = addOnIds == null || addOnIds.length == 0 ? "-" : String.join(",", addOnIds);
        return String.format("%-8s %-12s %-8s %-10s %-10s %-18s RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f %-15s",
                bookingId, customerId, vehicleId,
                startDate.format(FMT), endDate.format(FMT),
                status, baseCost, addOnCost, insuranceCost, lateFee, totalCost, addons);
    }

    private static String[] copyIds(String[] ids) {
        return ids == null ? new String[0] : Arrays.copyOf(ids, ids.length);
    }

    private static String[] append(String[] array, String value) {
        String[] out = Arrays.copyOf(array, array.length + 1);
        out[array.length] = value;
        return out;
    }
}
