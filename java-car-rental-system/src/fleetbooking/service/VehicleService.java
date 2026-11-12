package fleetbooking.service;

import fleetbooking.model.EconomyCar;
import fleetbooking.model.LuxuryCar;
import fleetbooking.model.SUV;
import fleetbooking.model.Vehicle;
import util.AppPaths;
import util.FileUtil;
import util.ValidationUtil;

public final class VehicleService {
    private static Vehicle[] vehicles = new Vehicle[20];
    private static int size = 0;
    private static boolean loaded = false;
    private static int vehicleCounter = 1;

    private VehicleService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        for (String line : FileUtil.readAllLines(AppPaths.VEHICLES_FILE)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 9) continue;
            Vehicle v = createVehicle(p[1], p[0], p[2], parseDouble(p[3]), parseInt(p[4]), p[5]);
            if (v == null) continue;
            v.setMileage(parseInt(p[6]));
            v.setMaintenanceStatus(Boolean.parseBoolean(p[7]));
            v.setStock(parseInt(p[8]));
            vehicles = addInternal(vehicles, v);
            size++;
        }
        syncCounter();
        loaded = true;
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) {
            Vehicle v = vehicles[i];
            lines[i] = String.join("|",
                    v.getCarID(), v.getVehicleType(), v.getModel(), String.valueOf(v.getDailyRate()),
                    String.valueOf(v.getSeatingCapacity()), v.getTransmission(), String.valueOf(v.getMileage()),
                    String.valueOf(v.isMaintenanceStatus()), String.valueOf(v.getStock()));
        }
        FileUtil.writeAllLines(AppPaths.VEHICLES_FILE, lines);
    }

    public static Vehicle[] listAll() {
        Vehicle[] out = new Vehicle[size];
        for (int i = 0; i < size; i++) out[i] = vehicles[i];
        return out;
    }

    public static boolean isEmpty() { 
        return size == 0; 
    }

    public static Vehicle getVehicleById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) {
            if (vehicles[i].getCarID().equalsIgnoreCase(target)) return vehicles[i];
        }
        return null;
    }

    public static Vehicle createVehicle(String type, String id, String model, double dailyRate, int seats, String transmission) {
        if (!ValidationUtil.isValidVehicleType(type)) return null;
        if (!ValidationUtil.isValidTransmission(transmission)) return null;
        String typeNorm = ValidationUtil.normalizeVehicleType(type);
        String transNorm = ValidationUtil.normalizeTransmission(transmission);
        switch (typeNorm) {
            case "economy": 
                return new EconomyCar(id, model, dailyRate, seats, transNorm);
            case "suv": 
                return new SUV(id, model, dailyRate, seats, transNorm);
            case "luxury": 
                return new LuxuryCar(id, model, dailyRate, seats, transNorm);
            default: 
                return null;
        }
    }

    public static String nextVehicleId() {
        syncCounter();
        return String.format("V%03d", vehicleCounter++);
    }

    public static boolean add(Vehicle vehicle) {
        if (vehicle == null || getVehicleById(vehicle.getCarID()) != null) return false;
        if (!ValidationUtil.isValidTransmission(vehicle.getTransmission())) return false;
        vehicle.setTransmission(ValidationUtil.normalizeTransmission(vehicle.getTransmission()));
        vehicles = addInternal(vehicles, vehicle);
        size++;
        saveToFile();
        syncCounter();
        return true;
    }

    public static boolean deleteById(String id) {
        if (id == null) return false;
        String target = id.trim();
        int idx = indexOf(target);
        if (idx < 0) return false;
        for (int i = idx; i < size - 1; i++) vehicles[i] = vehicles[i + 1];
        vehicles[size - 1] = null;
        size--;
        saveToFile();
        return true;
    }

    public static void adjustStock(String vehicleId, int delta) {
        Vehicle v = getVehicleById(vehicleId);
        if (v == null) return;
        v.setStock(v.getStock() + delta);
        saveToFile();
    }

    public static void setMaintenanceStatus(String vehicleId, boolean maintenance) {
        Vehicle v = getVehicleById(vehicleId);
        if (v == null) return;
        v.setMaintenanceStatus(maintenance);
        saveToFile();
    }

    public static void updateMileage(String vehicleId, int mileage) {
        Vehicle v = getVehicleById(vehicleId);
        if (v == null) return;
        v.setMileage(Math.max(0, mileage));
        saveToFile();
    }

    public static void syncCounter() {
        int max = 0;
        for (int i = 0; i < size; i++) {
            Vehicle v = vehicles[i];
            String id = v.getCarID();
            if (id != null && id.matches("V\\d{3}")) {
                try { max = Math.max(max, Integer.parseInt(id.substring(1))); } catch (Exception ignored) {}
            }
        }
        vehicleCounter = max + 1;
    }

    private static double parseDouble(String s) { 
        try { 
            return Double.parseDouble(s.trim()); 
        } 
        catch (Exception e) { 
            return 0; 
        } 
    }

    private static int parseInt(String s) { 
        try { 
            return Integer.parseInt(s.trim()); 
        } 
        catch (Exception e) { 
            return 0; 
        } 
    }

    public static boolean addDemoVehicle(Vehicle v) { 
        return add(v); 
    }

    private static int indexOf(String id) {
        for (int i = 0; i < size; i++) {
            if (vehicles[i].getCarID().equalsIgnoreCase(id)) return i;
        }
        return -1;
    }

    private static Vehicle[] addInternal(Vehicle[] arr, Vehicle v) {
        if (size >= arr.length) {
            Vehicle[] next = new Vehicle[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = v;
        return arr;
    }
}
