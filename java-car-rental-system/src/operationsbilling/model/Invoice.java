package operationsbilling.model;

import common.model.Transaction;
import fleetbooking.model.Booking;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Invoice extends Transaction {
    private static int counter = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String invoiceId;
    private String bookingId;
    private String customerId;
    private String vehicleId;
    private double baseRentalCost;
    private double addOnCost;
    private double insuranceCharge;
    private double lateReturnCharge;
    private double damagePenalty;
    private double fuelPenalty;
    private double totalAmount;
    private String inspectionId;
    private String inspectionRemarks;
    private String addOnIdsCsv;
    private LocalDateTime issuedAt;
    private ChargeItem[] chargeItems = new ChargeItem[0];

    public Invoice(Booking booking, CarInspection inspection, double damagePenalty, double fuelPenalty) {
        super(String.format("INV%04d", counter++), LocalDateTime.now());
        this.invoiceId = transactionId;
        this.bookingId = booking.getBookingId();
        this.customerId = booking.getCustomerId();
        this.vehicleId = booking.getVehicleId();
        this.baseRentalCost = booking.getBaseCost();
        this.addOnCost = booking.getAddOnCost();
        this.insuranceCharge = booking.getInsuranceCost();
        this.lateReturnCharge = booking.getLateFee();
        this.damagePenalty = Math.max(0, damagePenalty);
        this.fuelPenalty = Math.max(0, fuelPenalty);
        this.inspectionId = inspection == null ? "N/A" : inspection.getInspectionId();
        this.inspectionRemarks = inspection == null ? "No post-rental inspection recorded." : inspection.getRemarks();
        this.addOnIdsCsv = booking.getAddOnIds().length == 0 ? "" : String.join(",", booking.getAddOnIds());
        this.issuedAt = LocalDateTime.now();
        rebuildCharges();
    }

    private Invoice() {}

    private void rebuildCharges() {
        chargeItems = new ChargeItem[0];
        chargeItems = append(chargeItems, ChargeItem.createBaseRental(baseRentalCost));
        chargeItems = append(chargeItems, ChargeItem.createAddOnFee(addOnCost));
        chargeItems = append(chargeItems, ChargeItem.createInsuranceCharge(insuranceCharge));
        chargeItems = append(chargeItems, ChargeItem.createLateFee(lateReturnCharge));
        chargeItems = append(chargeItems, ChargeItem.createDamagePenalty(this.damagePenalty));
        chargeItems = append(chargeItems, ChargeItem.createFuelPenalty(this.fuelPenalty));
        
        totalAmount = 0;
        for (int i = 0; i < chargeItems.length; i++) {
            totalAmount += chargeItems[i].getAmount();
        }
    }

    // RESTORE FROM FILE - 14 parameters version (with insuranceCharge)
    public static Invoice restoreFromFile(String invoiceId, String bookingId, String customerId, String vehicleId,
                                          double baseRentalCost, double addOnCost, double insuranceCharge,
                                          double lateReturnCharge, double damagePenalty, double fuelPenalty, double totalAmount,
                                          String inspectionId, String inspectionRemarks, String addOnIdsCsv,
                                          LocalDateTime issuedAt) {
        Invoice inv = new Invoice();
        inv.transactionId = invoiceId;
        inv.invoiceId = invoiceId;
        inv.bookingId = bookingId;
        inv.customerId = customerId;
        inv.vehicleId = vehicleId;
        inv.baseRentalCost = baseRentalCost;
        inv.addOnCost = addOnCost;
        inv.insuranceCharge = insuranceCharge;
        inv.lateReturnCharge = lateReturnCharge;
        inv.damagePenalty = damagePenalty;
        inv.fuelPenalty = fuelPenalty;
        inv.totalAmount = totalAmount;
        inv.inspectionId = inspectionId;
        inv.inspectionRemarks = inspectionRemarks;
        inv.addOnIdsCsv = addOnIdsCsv == null ? "" : addOnIdsCsv;
        inv.issuedAt = issuedAt == null ? LocalDateTime.now() : issuedAt;
        inv.createdAt = inv.issuedAt;
        inv.rebuildCharges();
        return inv;
    }

    // RESTORE FROM FILE - 13 parameters version (without insuranceCharge, for backward compatibility)
    public static Invoice restoreFromFile(String invoiceId, String bookingId, String customerId, String vehicleId,
                                          double baseRentalCost, double addOnCost, double lateReturnCharge,
                                          double damagePenalty, double fuelPenalty, double totalAmount,
                                          String inspectionId, String inspectionRemarks, String addOnIdsCsv,
                                          LocalDateTime issuedAt) {
        return restoreFromFile(invoiceId, bookingId, customerId, vehicleId, 
                baseRentalCost, addOnCost, 0.0, lateReturnCharge, 
                damagePenalty, fuelPenalty, totalAmount, 
                inspectionId, inspectionRemarks, addOnIdsCsv, issuedAt);
    }

    public static void advanceCounter(int nextValue) { 
        if (nextValue > counter) counter = nextValue; 
    }
    
    public static void syncCounterFromInvoiceId(String invoiceId) {
        if (invoiceId == null) return;
        if (invoiceId.matches("INV\\d{4}")) {
            try { 
                advanceCounter(Integer.parseInt(invoiceId.substring(3)) + 1); 
            } catch (Exception ignored) {}
        }
    }

    // Getters
    public String getInvoiceId() { 
        return invoiceId; 
    }

    public String getBookingId() { 
        return bookingId; 
    }

    public String getCustomerId() { 
        return customerId; 
    }

    public String getVehicleId() { 
        return vehicleId; 
    }

    public double getBaseRentalCost() { 
        return baseRentalCost; 
    }

    public double getAddOnCost() { 
        return addOnCost; 
    }

    public double getInsuranceCharge() { 
        return insuranceCharge; 
    }

    public double getLateReturnCharge() { 
        return lateReturnCharge; 
    }

    public double getDamagePenalty() { 
        return damagePenalty; 
    }

    public double getFuelPenalty() { 
        return fuelPenalty; 
    }

    public double getTotalAmount() { 
        return totalAmount; 
    }

    public String getInspectionId() { 
        return inspectionId; 
    }

    public String getInspectionRemarks() { 
        return inspectionRemarks; 
    }

    public String getAddOnIdsCsv() { 
        return addOnIdsCsv; 
    }

    public LocalDateTime getIssuedAt() { 
        return issuedAt; 
    }

    public ChargeItem[] getChargeItems() { 
        return Arrays.copyOf(chargeItems, chargeItems.length); 
    }

    public String toFormattedString() {
        String line = "=".repeat(72);
        String thin = "-".repeat(72);
        StringBuilder chargesBreakdown = new StringBuilder();
        
        for (ChargeItem item : chargeItems) {
            if (item.getAmount() > 0) {
                chargesBreakdown.append(String.format("  %-25s : RM %10.2f%n", item.getDescription(), item.getAmount()));
            }
        }
        
        return "\n" + line + "\n" +
                "                     CAR RENTAL SYSTEM - FINAL INVOICE\n" +
                line + "\n" +
                String.format("  Invoice ID   : %s%n", invoiceId) +
                String.format("  Issued At    : %s%n", issuedAt.format(FMT)) +
                thin + "\n" +
                String.format("  Booking ID   : %s%n", bookingId) +
                String.format("  Customer ID  : %s%n", customerId) +
                String.format("  Vehicle ID   : %s%n", vehicleId) +
                thin + "\n" +
                "  CHARGES BREAKDOWN\n" +
                thin + "\n" +
                chargesBreakdown.toString() +
                thin + "\n" +
                String.format("  TOTAL AMOUNT DUE          : RM %10.2f%n", totalAmount) +
                line + "\n" +
                String.format("  Inspection ID : %s%n", inspectionId) +
                String.format("  Inspection    : %s%n", inspectionRemarks) +
                line + "\n";
    }

    public String toCsvLine() {
        return String.join("|",
                invoiceId, bookingId, customerId, vehicleId,
                String.format("%.2f", baseRentalCost),
                String.format("%.2f", addOnCost),
                String.format("%.2f", insuranceCharge),
                String.format("%.2f", lateReturnCharge),
                String.format("%.2f", damagePenalty),
                String.format("%.2f", fuelPenalty),
                String.format("%.2f", totalAmount),
                inspectionId,
                inspectionRemarks.replace("|", ";"),
                addOnIdsCsv.replace("|", ";"),
                issuedAt.format(FMT));
    }

    @Override
    public String toString() {
        return String.format("%-10s %-8s %-12s %-8s RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f RM%-8.2f",
                invoiceId, bookingId, customerId, vehicleId,
                baseRentalCost, addOnCost, insuranceCharge, lateReturnCharge, damagePenalty, fuelPenalty, totalAmount);
    }

    private static ChargeItem[] append(ChargeItem[] array, ChargeItem value) {
        if (value.getAmount() == 0) return array;
        ChargeItem[] out = Arrays.copyOf(array, array.length + 1);
        out[array.length] = value;
        return out;
    }
}