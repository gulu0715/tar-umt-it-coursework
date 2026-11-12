package util;

public final class ValidationUtil {
    private ValidationUtil() {}

    public static String normalizePhone(String raw) {
        if (raw == null) return "";
        String phone = raw.trim().replaceAll("[\\s\\-()]+", "");
        if (phone.startsWith("+")) phone = phone.substring(1);
        return phone;
    }

    public static boolean isValidPhone(String raw) {
        String phone = normalizePhone(raw);
        return phone.matches("60\\d{9,10}");
    }

    public static String normalizeLicenseNumber(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[\\s-]", "").trim().toUpperCase();
    }

    public static boolean isValidLicenseNumber(String raw) {
        String lic = normalizeLicenseNumber(raw);
        return lic.matches("[A-Z]{1,2}\\d{6,10}");
    }

    public static boolean isValidLicenseType(String raw) {
        if (raw == null) return false;
        return raw.equalsIgnoreCase("AUTO") || raw.equalsIgnoreCase("MANUAL");
    }

    public static String normalizeLicenseType(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.equalsIgnoreCase("AUTO")) return "AUTO";
        if (raw.equalsIgnoreCase("MANUAL")) return "MANUAL";
        return raw.toUpperCase();
    }

    public static boolean isValidVehicleType(String raw) {
        if (raw == null) return false;
        return raw.equalsIgnoreCase("Economy")
                || raw.equalsIgnoreCase("SUV")
                || raw.equalsIgnoreCase("Luxury");
    }

    public static String normalizeVehicleType(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.equalsIgnoreCase("Economy")) return "economy";
        if (raw.equalsIgnoreCase("SUV")) return "suv";
        if (raw.equalsIgnoreCase("Luxury")) return "luxury";
        return raw.toLowerCase();
    }

    public static boolean isValidTransmission(String raw) {
        if (raw == null) return false;
        return raw.equalsIgnoreCase("AUTO") || raw.equalsIgnoreCase("MANUAL");
    }

    public static String normalizeTransmission(String raw) {
        if (raw == null) return "";
        if (raw.equalsIgnoreCase("AUTO")) return "Auto";
        if (raw.equalsIgnoreCase("MANUAL")) return "Manual";
        return raw.trim();
    }
}
