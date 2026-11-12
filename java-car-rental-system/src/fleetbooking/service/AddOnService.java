package fleetbooking.service;

import fleetbooking.model.AddOn;
import util.AppPaths;
import util.FileUtil;

public final class AddOnService {
    private static AddOn[] addOns = new AddOn[20];
    private static int size = 0;
    private static boolean loaded = false;
    private static int addOnCounter = 1;

    private AddOnService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        for (String line : FileUtil.readAllLines(AppPaths.ADDONS_FILE)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 4) continue;
            String id = p[0].trim();
            String name = p[1].trim();
            double price = parseDouble(p[2]);
            boolean active = Boolean.parseBoolean(p[3]);
            String category = p.length > 4 ? p[4].trim() : "General";
            String description = p.length > 5 ? p[5].trim() : "";
            
            if (name.isEmpty() || price <= 0) continue;
            addOns = addInternal(addOns, new AddOn(id, name, price, active, category, description));
            size++;
        }
        syncCounter();
        loaded = true;
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) {
            AddOn a = addOns[i];
            lines[i] = String.join("|", 
                a.getAddOnId(), 
                a.getName(), 
                String.valueOf(a.getPricePerDay()), 
                String.valueOf(a.isActive()),
                a.getCategory(),
                a.getDescription()
            );
        }
        FileUtil.writeAllLines(AppPaths.ADDONS_FILE, lines);
    }

    public static AddOn[] listAll() {
        AddOn[] out = new AddOn[size];
        for (int i = 0; i < size; i++) out[i] = addOns[i];
        return out;
    }

    public static AddOn[] listActive() {
        AddOn[] temp = new AddOn[size];
        int count = 0;
        for (int i = 0; i < size; i++) if (addOns[i].isActive()) temp[count++] = addOns[i];
        AddOn[] out = new AddOn[count];
        for (int i = 0; i < count; i++) out[i] = temp[i];
        return out;
    }
    
    public static AddOn[] listByCategory(String category) {
        if (category == null) return new AddOn[0];
        AddOn[] temp = new AddOn[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (addOns[i].isActive() && addOns[i].getCategory().equalsIgnoreCase(category)) {
                temp[count++] = addOns[i];
            }
        }
        AddOn[] out = new AddOn[count];
        for (int i = 0; i < count; i++) out[i] = temp[i];
        return out;
    }

    public static boolean isEmpty() { return size == 0; }

    public static AddOn getById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) if (addOns[i].getAddOnId().equalsIgnoreCase(target)) return addOns[i];
        return null;
    }

    public static AddOn getByName(String name) {
        if (name == null) return null;
        String target = name.trim();
        for (int i = 0; i < size; i++) if (addOns[i].getName().equalsIgnoreCase(target)) return addOns[i];
        return null;
    }

    public static String nextId() {
        syncCounter();
        return String.format("A%03d", addOnCounter++);
    }

    public static boolean add(AddOn addOn) {
        if (addOn == null || getById(addOn.getAddOnId()) != null) return false;
        if (addOn.getName() == null || addOn.getName().trim().isEmpty() || addOn.getPricePerDay() <= 0) return false;
        addOns = addInternal(addOns, addOn);
        size++;
        saveToFile();
        syncCounter();
        return true;
    }

    public static boolean update(AddOn updated) {
        if (updated == null) return false;
        if (updated.getName() == null || updated.getName().trim().isEmpty() || updated.getPricePerDay() <= 0) return false;
        for (int i = 0; i < size; i++) {
            if (addOns[i].getAddOnId().equalsIgnoreCase(updated.getAddOnId())) {
                addOns[i] = updated;
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static boolean deleteById(String id) {
        if (id == null) return false;
        String target = id.trim();
        int idx = indexOf(target);
        if (idx < 0) return false;
        for (int i = idx; i < size - 1; i++) addOns[i] = addOns[i + 1];
        addOns[size - 1] = null;
        size--;
        saveToFile();
        return true;
    }

    public static double calculateCost(String[] addOnIds, long bookedDays) {
        double total = 0;
        if (addOnIds == null || bookedDays <= 0) return 0;
        for (int i = 0; i < addOnIds.length; i++) {
            AddOn a = getById(addOnIds[i]);
            if (a != null && a.isActive()) total += a.getPricePerDay() * bookedDays;
        }
        return total;
    }

    public static String describe(String[] addOnIds) {
        if (addOnIds == null || addOnIds.length == 0) return "-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addOnIds.length; i++) {
            AddOn a = getById(addOnIds[i]);
            if (a != null) {
                if (sb.length() > 0) sb.append(",");
                sb.append(a.getName());
            }
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }
    
    public static String getCategoriesList() {
        String[] categories = {"Navigation", "Insurance", "Child Safety", "Connectivity", "Utility", "Safety", "General"};
        return String.join(", ", categories);
    }
    
    public static void printAddOnCategories() {
        System.out.println("\nAvailable Add-On Categories:");
        System.out.println("  - Navigation: GPS, Map updates");
        System.out.println("  - Insurance: Coverage, Protection plans");
        System.out.println("  - Child Safety: Baby seats, Booster seats");
        System.out.println("  - Connectivity: WiFi, Phone chargers");
        System.out.println("  - Utility: Roof rack, Cargo box");
        System.out.println("  - Safety: Snow chains, Emergency kit");
        System.out.println("  - General: Other accessories");
    }

    public static void syncCounter() {
        int max = 0;
        for (int i = 0; i < size; i++) {
            String id = addOns[i].getAddOnId();
            if (id != null && id.matches("A\\d{3}")) {
                try { max = Math.max(max, Integer.parseInt(id.substring(1))); } catch (Exception ignored) {}
            }
        }
        addOnCounter = max + 1;
    }

    private static double parseDouble(String s) { 
        try { return Double.parseDouble(s.trim()); 
        } 
        catch (Exception e) { 
            return 0; 
        } 
    }

    public static boolean addDemoAddOn(AddOn a) { 
        return add(a); 
    }

    private static int indexOf(String id) {
        for (int i = 0; i < size; i++) if (addOns[i].getAddOnId().equalsIgnoreCase(id)) return i;
        return -1;
    }

    private static AddOn[] addInternal(AddOn[] arr, AddOn a) {
        if (size >= arr.length) {
            AddOn[] next = new AddOn[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = a;
        return arr;
    }
}