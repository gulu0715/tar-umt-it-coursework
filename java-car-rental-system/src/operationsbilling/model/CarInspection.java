package operationsbilling.model;

import common.model.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CarInspection extends Transaction {
    public static final String FUEL_FULL = "Full";
    public static final String FUEL_THREE_QUARTER = "ThreeQuarter";
    public static final String FUEL_HALF = "Half";
    public static final String FUEL_QUARTER = "Quarter";
    public static final String FUEL_EMPTY = "Empty";

    public static final String DAMAGE_NONE = "None";
    public static final String DAMAGE_MINOR = "Minor";
    public static final String DAMAGE_MODERATE = "Moderate";
    public static final String DAMAGE_SEVERE = "Severe";

    private static int counter = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String inspectionId;
    private String vehicleId;
    private String bookingId;
    private String inspectorUsername;
    private String fuelLevel;
    private String damageSeverity;
    private String damageDescription;
    private boolean maintenanceRequired;
    private boolean rentable;
    private String remarks;
    private LocalDateTime inspectedAt;

    public CarInspection(String vehicleId, String bookingId, String inspectorUsername,
                         String fuelLevel, String damageSeverity, String damageDescription,
                         boolean maintenanceRequired) {
        super(String.format("INS%04d", counter++), LocalDateTime.now());
        this.inspectionId = transactionId;
        this.vehicleId = vehicleId;
        this.bookingId = (bookingId != null && !bookingId.isBlank()) ? bookingId : "N/A";
        this.inspectorUsername = inspectorUsername;
        this.fuelLevel = fuelLevel;
        this.damageSeverity = damageSeverity;
        this.damageDescription = (damageDescription != null && !damageDescription.isBlank()) ? damageDescription : "None";
        this.maintenanceRequired = maintenanceRequired;
        this.inspectedAt = LocalDateTime.now();
        evaluateRentability();
    }

   

    public static CarInspection restoreFromFile(String savedId, String vehicleId, String bookingId,
                                                String inspectorUsername, String fuelLevel, String damageSeverity,
                                                String damageDescription, boolean maintenanceRequired,
                                                boolean rentable, String remarks, LocalDateTime inspectedAt) {
        CarInspection ins = new CarInspection(vehicleId, bookingId, inspectorUsername, fuelLevel, damageSeverity, damageDescription, maintenanceRequired);
        ins.transactionId = savedId;
        ins.inspectionId = savedId;
        ins.rentable = rentable;
        ins.remarks = (remarks == null || remarks.isBlank()) ? ins.remarks : remarks;
        if (inspectedAt != null) ins.inspectedAt = inspectedAt;
        return ins;
    }

    public static void advanceCounter(int nextValue) { 
        if (nextValue > counter) counter = nextValue; 
    }

    public static void syncCounterFromInspectionId(String inspectionId) {
        if (inspectionId == null)return;
        if (inspectionId.matches("INS\\d{4}")) {
            try { advanceCounter(Integer.parseInt(inspectionId.substring(3)) + 1); } catch (Exception ignored) {}
        }
    }

   public void evaluateRentability() {
    if (maintenanceRequired) {
        rentable = false;
        remarks = "Vehicle requires maintenance before rental.";
    } else if (DAMAGE_SEVERE.equalsIgnoreCase(damageSeverity)) {
        rentable = false;
        remarks = "Severe damage detected. Vehicle cannot be rented until repaired.";
    } else if (FUEL_EMPTY.equalsIgnoreCase(fuelLevel)) {
        rentable = false;
        remarks = "Fuel level is empty. Vehicle must be refuelled before rental.";
    } else {
        rentable = true;
        if (DAMAGE_MODERATE.equalsIgnoreCase(damageSeverity))
            remarks = "Moderate damage noted. Approved for rental with caution.";
        else if (DAMAGE_MINOR.equalsIgnoreCase(damageSeverity))
            remarks = "Minor damage noted. Vehicle is rentable.";
        else
            remarks = "Vehicle passed inspection. Ready for rental.";
    }
}
    public String getInspectionId() { return inspectionId; }
    public String getVehicleId() { return vehicleId; }
    public String getBookingId() { return bookingId; }
    public String getInspectorUsername() { return inspectorUsername; }
    public String getFuelLevel() { return fuelLevel; }
    public String getDamageSeverity() { return damageSeverity; }
    public String getDamageDescription() { return damageDescription; }
    public boolean isMaintenanceRequired() { return maintenanceRequired; }
    public boolean isRentable() { return rentable; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getInspectedAt() { return inspectedAt; }

    public String toCsvLine() {
        return String.join("|",
                inspectionId,
                vehicleId,
                bookingId,
                inspectorUsername,
                fuelLevel,
                damageSeverity,
                damageDescription.replace("|", ";"),
                String.valueOf(maintenanceRequired),
                String.valueOf(rentable),
                remarks.replace("|", ";"),
                inspectedAt.format(FMT));
    }

    @Override
    public String toString() {
        return String.format("%-10s %-8s %-8s %-12s %-14s %-10s %-8s %-8s %-28s %-19s",
                inspectionId, vehicleId, bookingId, inspectorUsername, fuelLevel, damageSeverity,
                maintenanceRequired ? "Yes" : "No", rentable ? "YES" : "NO",
                remarks.length() > 28 ? remarks.substring(0, 25) + "..." : remarks,
                inspectedAt.format(FMT));
    }

    public void setFuelLevel(String fuelLevel) {
    this.fuelLevel = fuelLevel;
    }

    public void setDamageSeverity(String damageSeverity) {
        this.damageSeverity = damageSeverity;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public void setMaintenanceRequired(boolean maintenanceRequired) {
        this.maintenanceRequired = maintenanceRequired;
    }


   
}
