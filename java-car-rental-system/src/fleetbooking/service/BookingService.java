package fleetbooking.service;

import fleetbooking.model.Booking;
import fleetbooking.model.Vehicle;
import java.time.LocalDate;
import operationsbilling.service.InspectionService;
import useraccount.model.Customer;
import useraccount.repository.CustomerRepository;
import util.AppPaths;
import util.FileUtil;

public final class BookingService {
    private static Booking[] bookings = new Booking[30];
    private static int size = 0;
    private static boolean loaded = false;

    private BookingService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        for (String line : FileUtil.readAllLines(AppPaths.BOOKINGS_FILE)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 15) continue;
            double insuranceCost = 0.0;
            if (p.length >= 16) {
                insuranceCost = parseDouble(p[15]);
            } else {
                Vehicle v = VehicleService.getVehicleById(p[2]);
                long days = parseLong(p[6]);
                if (v != null && days > 0) {
                    insuranceCost = v.calculateInsurance(days);
                }
            }
            Booking b = Booking.restoreFromFile(
                    p[0], p[1], p[2],
                    LocalDate.parse(p[3]), LocalDate.parse(p[4]),
                    parseDouble(p[5]), parseLong(p[6]),
                    parseDouble(p[7]), parseDouble(p[8]), insuranceCost, parseDouble(p[9]), parseDouble(p[10]),
                    p[11], p[12].isEmpty() ? null : LocalDate.parse(p[12]),
                    p[13].isEmpty() ? null : LocalDate.parse(p[13]),
                    Booking.parseAddOnIds(p[14])
            );
            bookings = addInternal(bookings, b);
            size++;
        }
        syncCounter();
        loaded = true;
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) lines[i] = bookings[i].toCsvLine();
        FileUtil.writeAllLines(AppPaths.BOOKINGS_FILE, lines);
    }

    public static Booking[] listAll() {
        Booking[] out = new Booking[size];
        for (int i = 0; i < size; i++) out[i] = bookings[i];
        return out;
    }

    public static boolean isEmpty() { 
        return size == 0; 
    }

    public static Booking getBookingById(String bookingId) {
        if (bookingId == null) return null;
        String target = bookingId.trim();
        for (int i = 0; i < size; i++) if (bookings[i].getBookingId().equalsIgnoreCase(target)) return bookings[i];
        return null;
    }

    public static Booking[] listByCustomer(String customerId) {
        if (customerId == null) return new Booking[0];
        String target = customerId.trim();
        Booking[] temp = new Booking[size];
        int count = 0;
        for (int i = 0; i < size; i++) if (bookings[i].getCustomerId().equalsIgnoreCase(target)) temp[count++] = bookings[i];
        Booking[] out = new Booking[count];
        for (int i = 0; i < count; i++) out[i] = temp[i];
        return out;
    }

    public static Booking createBooking(String customerId, String vehicleId, LocalDate start, LocalDate end, String[] addOnIds) {
        Customer c = CustomerRepository.getById(customerId);
        Vehicle v = VehicleService.getVehicleById(vehicleId);
        if (c == null) {
            System.out.println("Customer not found.");
            return null;
        }
        if (v == null) {
            System.out.println("Vehicle not found.");
            return null;
        }
        if (start == null || end == null) {
            System.out.println("Booking dates are required.");
            return null;
        }
        if (end.isBefore(start)) {
            System.out.println("End date cannot be before start date.");
            return null;
        }
        if (v.isMaintenanceStatus()) {
            System.out.println("Cannot book: vehicle under maintenance.");
            return null;
        }
        if (InspectionService.findLatestByVehicleId(vehicleId) != null
                && !InspectionService.findLatestByVehicleId(vehicleId).isRentable()) {
            System.out.println("Cannot book: latest inspection marks this vehicle as not rentable.");
            return null;
        }
        if (v.getStock() <= 0) {
            System.out.println("Cannot book: vehicle out of stock.");
            return null;
        }
        if ("AUTO".equalsIgnoreCase(c.getLicenseType()) && "Manual".equalsIgnoreCase(v.getTransmission())) {
            System.out.println("Your license type is AUTO, so you cannot book a manual car.");
            return null;
        }

        if (addOnIds != null) {
            for (int i = 0; i < addOnIds.length; i++) {
                String id = addOnIds[i];
                if (id == null || AddOnService.getById(id) == null || !AddOnService.getById(id).isActive()) {
                    System.out.println("Invalid Add-On ID: " + id);
                    return null;
                }
            }
        }

        long bookedDays = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        if (bookedDays < 1) bookedDays = 1;
        double addOnCost = AddOnService.calculateCost(addOnIds, bookedDays);
        double insuranceCost = v.calculateInsurance(bookedDays);
        Booking b = new Booking(customerId, vehicleId, start, end, v.getDailyRate(), addOnIds, addOnCost, insuranceCost);
        bookings = addInternal(bookings, b);
        size++;
        VehicleService.adjustStock(vehicleId, -1);
        saveToFile();
        return b;
    }

    public static boolean approveBooking(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        boolean ok = b.approve();
        if (ok) saveToFile();
        return ok;
    }

    public static boolean rejectBooking(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        boolean ok = b.reject();
        if (ok) {
            VehicleService.adjustStock(b.getVehicleId(), 1);
            saveToFile();
        }
        return ok;
    }

    public static boolean requestCheckout(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        boolean ok = b.requestCheckout();
        if (ok) saveToFile();
        return ok;
    }

    public static boolean confirmCheckout(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        if (!Booking.STATUS_PENDING_CHECKOUT.equalsIgnoreCase(b.getStatus())) {
            System.out.println("Cannot confirm checkout. Customer must request checkout first.");
            return false;
        }
        boolean ok = b.confirmCheckout();
        if (ok) saveToFile();
        return ok;
    }

    public static boolean pickupVehicle(String bookingId, LocalDate pickupDate) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        if (pickupDate == null) {
            System.out.println("Pickup date is required.");
            return false;
        }
        boolean ok = b.pickupVehicle(pickupDate);
        if (ok) saveToFile();
        return ok;
    }

    public static boolean requestReturn(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        boolean ok = b.requestReturn();
        if (ok) saveToFile();
        return ok;
    }

    public static double confirmReturn(String bookingId, LocalDate actualReturnDate, boolean physicallyReturned) {
        Booking b = getBookingById(bookingId);
        if (b == null) return Double.NaN;
        if (!physicallyReturned) {
            System.out.println("Return confirmation cancelled.");
            return Double.NaN;
        }
        if (!Booking.STATUS_PENDING_RETURN.equalsIgnoreCase(b.getStatus())) {
            System.out.println("Cannot confirm return. Customer must request return first.");
            return Double.NaN;
        }
        double late = b.confirmReturn(actualReturnDate);
        if (Double.isNaN(late)) {
            System.out.println("Invalid return date.");
            return Double.NaN;
        }
        VehicleService.adjustStock(b.getVehicleId(), 1);
        saveToFile();
        return late;
    }

    public static boolean cancelBooking(String bookingId) {
        Booking b = getBookingById(bookingId);
        if (b == null) return false;
        boolean ok = b.cancel();
        if (ok) {
            VehicleService.adjustStock(b.getVehicleId(), 1);
            saveToFile();
        }
        return ok;
    }

    public static void addDemoBooking(Booking booking) {
        if (booking == null) return;
        bookings = addInternal(bookings, booking);
        size++;
        saveToFile();
    }

    public static void syncCounter() {
        int max = 0;
        for (int i = 0; i < size; i++) {
            String id = bookings[i].getBookingId();
            if (id != null && id.matches("B\\d{4}")) {
                try { max = Math.max(max, Integer.parseInt(id.substring(1))); } catch (Exception ignored) {}
            }
        }
        Booking.advanceCounter(max + 1);
    }

    private static double parseDouble(String s) { 
        try { 
            return Double.parseDouble(s.trim()); 
        } 
        catch (Exception e) { 
            return 0; 
        } 
    }
    private static long parseLong(String s) { 
        try { 
            return Long.parseLong(s.trim()); 
        } 
        catch (Exception e) { 
            return 0; 
        } 
    }

    private static Booking[] addInternal(Booking[] arr, Booking b) {
        if (size >= arr.length) {
            Booking[] next = new Booking[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = b;
        return arr;
    }
}
