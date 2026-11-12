package operationsbilling.service;

import fleetbooking.model.Booking;
import fleetbooking.model.Vehicle;
import fleetbooking.service.BookingService;
import fleetbooking.service.VehicleService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import operationsbilling.model.CarInspection;
import util.AppPaths;
import util.FileUtil;
import util.InputUtil;

public final class InspectionService {
    public static final double PENALTY_MINOR_DAMAGE = 50.00;
    public static final double PENALTY_MODERATE_DAMAGE = 200.00;
    public static final double PENALTY_SEVERE_DAMAGE = 500.00;
    public static final double PENALTY_FUEL_QUARTER = 30.00;
    public static final double PENALTY_FUEL_HALF = 20.00;
    public static final double PENALTY_FUEL_THREE_QTR = 10.00;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static CarInspection[] inspections = new CarInspection[20];
    private static int size = 0;
    private static boolean loaded = false;
   
    private InspectionService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        for (String line : FileUtil.readAllLines(AppPaths.INSPECTIONS_FILE)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 11) continue;
            LocalDateTime time = null;
            try { 
                time = LocalDateTime.parse(p[10], FMT);  
            } catch (Exception ignored) {

            }
            CarInspection ins = CarInspection.restoreFromFile(p[0], p[1], p[2], p[3], p[4], p[5], p[6],
                    Boolean.parseBoolean(p[7]), Boolean.parseBoolean(p[8]), p[9], time);
            inspections = addInternal(inspections, ins);
            size++;
            CarInspection.syncCounterFromInspectionId(ins.getInspectionId());
        }
        loaded = true;
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) lines[i] = inspections[i].toCsvLine();
        FileUtil.writeAllLines(AppPaths.INSPECTIONS_FILE, lines);
    }

    public static CarInspection[] listAll() {
        CarInspection[] out = new CarInspection[size];
        for (int i = 0; i < size; i++) out[i] = inspections[i];
        return out;
    }

    public static CarInspection getById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) if (inspections[i].getInspectionId().equalsIgnoreCase(target)) return inspections[i];
        return null;
    }

    public static CarInspection findLatestByBookingId(String bookingId) {
        CarInspection latest = null;
        if (bookingId == null) return null;
        String target = bookingId.trim();
        for (int i = 0; i < size; i++) if (inspections[i].getBookingId().equalsIgnoreCase(target)) latest = inspections[i];
        return latest;
    }

    public static CarInspection findLatestByVehicleId(String vehicleId) {
        CarInspection latest = null;
        if (vehicleId == null) return null;
        String target = vehicleId.trim();
        for (int i = 0; i < size; i++) if (inspections[i].getVehicleId().equalsIgnoreCase(target)) latest = inspections[i];
        return latest;
    }

    private static void syncVehicleMaintenanceStatus(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) return;
        CarInspection latest = findLatestByVehicleId(vehicleId);
        boolean underMaintenance = latest != null && !latest.isRentable();
        VehicleService.setMaintenanceStatus(vehicleId, underMaintenance);
    }

    public static double calculatePenalty(CarInspection inspection) {
        if (inspection == null) return 0;
        double penalty = 0;
        switch (inspection.getDamageSeverity()) {
            case CarInspection.DAMAGE_MINOR: 
                penalty += PENALTY_MINOR_DAMAGE; 
                break;
            case CarInspection.DAMAGE_MODERATE: 
                penalty += PENALTY_MODERATE_DAMAGE; 
                break;
            case CarInspection.DAMAGE_SEVERE: 
                penalty += PENALTY_SEVERE_DAMAGE; 
                break;
        }

        switch (inspection.getFuelLevel()) {
            case CarInspection.FUEL_THREE_QUARTER: 
                penalty += PENALTY_FUEL_THREE_QTR; 
                break;
            case CarInspection.FUEL_HALF: 
                penalty += PENALTY_FUEL_HALF; 
                break;
            case CarInspection.FUEL_QUARTER: 
                penalty += PENALTY_FUEL_QUARTER; 
                break;
            case CarInspection.FUEL_EMPTY: 
                penalty += PENALTY_FUEL_QUARTER * 2; 
                break;
        }
        return penalty;
    }

    public static double damagePenalty(CarInspection inspection) {
        if (inspection == null) return 0;
        switch (inspection.getDamageSeverity()) {
            case CarInspection.DAMAGE_MINOR: 
                return PENALTY_MINOR_DAMAGE;
            case CarInspection.DAMAGE_MODERATE: 
                return PENALTY_MODERATE_DAMAGE;
            case CarInspection.DAMAGE_SEVERE: 
                return PENALTY_SEVERE_DAMAGE;
            default: 
                return 0;
        }
    }

    public static double fuelPenalty(CarInspection inspection) {
        if (inspection == null) return 0;
        switch (inspection.getFuelLevel()) {
            case CarInspection.FUEL_THREE_QUARTER: 
                return PENALTY_FUEL_THREE_QTR;
            case CarInspection.FUEL_HALF: 
                return PENALTY_FUEL_HALF;
            case CarInspection.FUEL_QUARTER: 
                return PENALTY_FUEL_QUARTER;
            case CarInspection.FUEL_EMPTY: 
                return PENALTY_FUEL_QUARTER * 2;
            default: 
                return 0;
        }
    }

    public static CarInspection conductInspection(String adminUsername, Scanner sc) {
        printInspectableVehicles();
        System.out.println("\n===== CAR INSPECTION FORM =====");

        String vehicleId = InputUtil.readBackableValidatedString(sc, "Enter Vehicle ID to inspect (0 to go back): ",
                s -> VehicleService.getVehicleById(s) != null,
                "Vehicle not found. Please enter a valid Vehicle ID.");
        Vehicle v = VehicleService.getVehicleById(vehicleId);
        if (v == null) return null;
        System.out.println("Vehicle selected: " + v.getModel() + " [" + v.getVehicleType() + "]");

        System.out.println("Bookings linked to this vehicle:");
        printLinkedBookings(vehicleId);
        String bookingId = InputUtil.readOptionalValidatedString(sc, "Link to Booking ID (press Enter to skip): ",
                s -> BookingService.getBookingById(s) != null,
                "Booking not found. Please enter a valid Booking ID or press Enter to skip.");

        int fuelChoice = InputUtil.readIntInRange(sc, "Fuel Level (1 Full, 2 ThreeQuarter, 3 Half, 4 Quarter, 5 Empty): ", 1, 5);
        String fuel = parseFuel(fuelChoice);
        int damageChoice = InputUtil.readIntInRange(sc, "Damage Severity (1 None, 2 Minor, 3 Moderate, 4 Severe): ", 1, 4);
        String damage = parseDamage(damageChoice);
        String desc = CarInspection.DAMAGE_NONE.equalsIgnoreCase(damage)
                ? "None"
                : InputUtil.readNonEmptyString(sc, "Damage description: ");
        boolean maintenance = InputUtil.readYesNo(sc, "Maintenance required? (Y/N): ");

        CarInspection inspection = new CarInspection(vehicleId, bookingId, adminUsername, fuel, damage, desc, maintenance);
        inspections = addInternal(inspections, inspection);
        size++;
        saveToFile();
        syncVehicleMaintenanceStatus(vehicleId);
        return inspection;
    }

    public static boolean addInspection(CarInspection inspection) {
        if (inspection == null) return false;
        inspections = addInternal(inspections, inspection);
        size++;
        saveToFile();
        syncVehicleMaintenanceStatus(inspection.getVehicleId());
        return true;
    }

    public static void printPenaltyBreakdown(CarInspection inspection) {
        System.out.println("--- Penalty Breakdown ---");
        System.out.println("Damage penalty (" + inspection.getDamageSeverity() + "): RM " + String.format("%.2f", damagePenalty(inspection)));
        System.out.println("Fuel penalty (" + inspection.getFuelLevel() + "): RM " + String.format("%.2f", fuelPenalty(inspection)));
        System.out.println("Total penalty: RM " + String.format("%.2f", calculatePenalty(inspection)));
    }

    public static void viewAllInspections() {
        System.out.println("=".repeat(140));
        System.out.println(String.format("%-10s %-8s %-8s %-12s %-14s %-10s %-8s %-8s %-30s %-19s",
                "INS-ID", "VEH-ID", "BKG-ID", "INSPECTOR", "FUEL", "DAMAGE", "MAINT", "RENTABLE", "REMARKS", "TIME"));
        System.out.println("=".repeat(140));
        if (size == 0) {
            System.out.println("No inspection records.");
        } else {
            for (int i = 0; i < size; i++) System.out.println(inspections[i]);
        }
        System.out.println("=".repeat(140));
    }

    private static void printInspectableVehicles() {
        System.out.println("=".repeat(100));
        System.out.println(String.format("%-8s %-22s %-10s %-10s %-5s %-8s %-8s %-10s %-10s", "VEH-ID", "MODEL", "TYPE", "TRANS", "SEATS", "MILE", "STOCK", "MAINT","Price(RM)"));
        System.out.println("=".repeat(100));
        for (Vehicle v : VehicleService.listAll()) System.out.println(v);
        System.out.println("=".repeat(100));
    }

    private static void printLinkedBookings(String vehicleId) {
        System.out.println("=".repeat(120));
        System.out.println(String.format("%-8s %-12s %-8s %-10s %-10s %-18s RM%-8s RM%-8s RM%-8s RM%-8s %-15s",
                "BID", "CUST", "VEH", "START", "END", "STATUS", "BASE", "ADDON", "LATE", "TOTAL", "ADDONS"));
        System.out.println("=".repeat(120));
        boolean found = false;
        for (Booking b : BookingService.listAll()) {
            if (b.getVehicleId().equalsIgnoreCase(vehicleId)) {
                found = true;
                System.out.println(b);
            }
        }
        if (!found) System.out.println("No bookings linked.");
        System.out.println("=".repeat(120));
    }

    public static String parseFuel(int choice) {
        switch (choice) {
            case 1: 
                return CarInspection.FUEL_FULL;
            case 2:
                return CarInspection.FUEL_THREE_QUARTER;
            case 3: 
                return CarInspection.FUEL_HALF;
            case 4:
                return CarInspection.FUEL_QUARTER;
            case 5:
                return CarInspection.FUEL_EMPTY;
            default: 
                return CarInspection.FUEL_FULL;
        }
    }

    public static String parseDamage(int choice) {
        switch (choice) {
            case 1: 
                return CarInspection.DAMAGE_NONE;
            case 2: 
                return CarInspection.DAMAGE_MINOR;
            case 3: 
                return CarInspection.DAMAGE_MODERATE;
            case 4: 
                return CarInspection.DAMAGE_SEVERE;
            default: 
                return CarInspection.DAMAGE_NONE;
        }
    }

    private static CarInspection[] addInternal(CarInspection[] arr, CarInspection value) {
        if (size >= arr.length) {
            CarInspection[] next = new CarInspection[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = value;
        return arr;
    }

    public static boolean deleteInspection(String inspectionId) {
        if (inspectionId == null) return false;
        String target = inspectionId.trim();
        int idx = indexOf(target);
        if (idx < 0) return false;

        String vehicleId = inspections[idx].getVehicleId();
        for (int i = idx; i < size - 1; i++) {
            inspections[i] = inspections[i + 1];
        }
        inspections[size - 1] = null;
        size--;
        saveToFile();
        syncVehicleMaintenanceStatus(vehicleId);
        return true;
    }

    public static boolean updateInspection(String inspectionId,
                                           String fuelLevel,
                                           String damageSeverity,
                                           String damageDescription,
                                           boolean maintenanceRequired) {
        CarInspection ins = getById(inspectionId);
        if (ins == null) return false;

        ins.setFuelLevel(fuelLevel);
        ins.setDamageSeverity(damageSeverity);
        ins.setDamageDescription(damageDescription);
        ins.setMaintenanceRequired(maintenanceRequired);
        ins.evaluateRentability();

        saveToFile();
        syncVehicleMaintenanceStatus(ins.getVehicleId());
        return true;
    }

    private static int indexOf(String inspectionId) {
        for (int i = 0; i < size; i++) {
            if (inspections[i].getInspectionId().equalsIgnoreCase(inspectionId)) {
                return i;
            }
        }
        return -1;
    }

}
