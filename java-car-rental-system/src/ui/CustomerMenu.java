package ui;

import fleetbooking.model.AddOn;
import fleetbooking.model.Booking;
import fleetbooking.model.Vehicle;
import fleetbooking.service.AddOnService;
import fleetbooking.service.BookingService;
import fleetbooking.service.VehicleService;
import java.time.LocalDate;
import java.util.Scanner;
import useraccount.model.User;
import util.InputUtil;

public final class CustomerMenu {
    private static final Scanner sc = util.InputUtil.SC;

    private CustomerMenu() {}

    public static void start(User user) {
        int choice;
        do {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. View Vehicles");
            System.out.println("2. Book Vehicle");
            System.out.println("3. View My Bookings");
            System.out.println("4. Checkout / Pick Up Vehicle");
            System.out.println("5. Request Return");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Logout");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    viewVehicles(); 
                    break;
                case 2: 
                    bookVehicle(user); 
                    break;
                case 3: 
                    viewMyBookings(user); 
                    break;
                case 4: 
                    checkoutFlow(user); 
                    break;
                case 5: 
                    requestReturn(user); 
                    break;
                case 6: 
                    cancelBooking(user); 
                    break;
                case 7: 
                    System.out.println("Logout."); 
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    private static void viewVehicles() {
        System.out.println("=".repeat(105));
        System.out.println(String.format("%-8s %-22s %-10s %-10s %-5s %-8s %-8s %-10s %-10s", "VEH-ID", "MODEL", "TYPE", "TRANS", "SEATS", "MILE", "STOCK", "MAINT","Price"));
        System.out.println("=".repeat(105));
        for (Vehicle v : VehicleService.listAll()) System.out.println(v);
        System.out.println("=".repeat(105));
    }

    private static void viewAddOns() {
        System.out.println("=".repeat(55));
        System.out.println(String.format("%-8s %-20s %-12s %-8s", "ADD-ID", "NAME", "PRICE/DAY", "ACTIVE"));
        System.out.println("=".repeat(55));
        for (AddOn a : AddOnService.listActive()) System.out.println(a);
        System.out.println("=".repeat(55));
    }

    private static void viewMyBookings(User user) {
        System.out.println("=".repeat(141));
        System.out.println(String.format("%-8s %-12s %-8s %-10s %-10s %-18s %-10s %-10s %-10s %-10s %-10s %-15s",
                "BID", "CUST", "VEH", "START", "END", "STATUS", "BASE(RM)", "ADDON(RM)", "LATE(RM)","Price", "TOTAL(RM)", "ADDONS"));
        System.out.println("=".repeat(141));
        boolean found = false;
        for (Booking b : BookingService.listByCustomer(user.getUsername())) {
            found = true;
            System.out.println(b);
        }
        if (!found) System.out.println("No bookings.");
        System.out.println("=".repeat(141));
    }

    private static void bookVehicle(User user) {
        viewVehicles();
        String vehicleId = InputUtil.readBackableValidatedString(sc, "Enter Vehicle ID to book (0 to go back): ",
                s -> VehicleService.getVehicleById(s) != null,
                "Vehicle not found. Please enter a valid Vehicle ID.");

        LocalDate start = InputUtil.readDate(sc, "Start date (yyyy-mm-dd): ");
        LocalDate end;
        do {
            end = InputUtil.readDate(sc, "End date (yyyy-mm-dd): ");
            if (end.isBefore(start)) System.out.println("End date cannot be before start date.");
        } while (end.isBefore(start));

        String[] addOnIds = new String[0];
        while (true) {
            viewAddOns();
            String addOnsRaw = InputUtil.readOptionalString(sc, "Enter Add-On IDs separated by comma (press Enter for none): ");
            if (addOnsRaw.isBlank()) break;
            String[] tmp = splitCsv(addOnsRaw);
            boolean valid = true;
            for (int i = 0; i < tmp.length; i++) {
                String id = tmp[i];
                AddOn addOn = AddOnService.getById(id);
                if (addOn == null || !addOn.isActive()) {
                    System.out.println("Invalid Add-On ID: " + id);
                    valid = false;
                    break;
                }
            }
            if (valid) {
                addOnIds = tmp;
                break;
            }
        }

        Booking booking = BookingService.createBooking(user.getUsername(), vehicleId, start, end, addOnIds);
        if (booking != null) {
            System.out.println("Booking created. Booking ID: " + booking.getBookingId());
            System.out.println(String.format("%-8s %-12s %-8s %-10s %-10s %-18s %-10s %-10s %-10s %-10s %-10s %-15s",
                "BID", "CUST", "VEH", "START", "END", "STATUS", "BASE(RM)", "ADDON(RM)", "LATE(RM)","Price", "TOTAL(RM)", "ADDONS"));
        
            System.out.println(booking);
            
        }
    }

    private static void checkoutFlow(User user) {
        Booking booking = selectMyBookingOrBack(user, "Enter Booking ID (0 to go back): ");
        if (booking == null) return;

        switch (booking.getStatus()) {
            case Booking.STATUS_APPROVED:
                if (BookingService.requestCheckout(booking.getBookingId())) {
                    System.out.println("Checkout requested; waiting for admin confirmation.");
                } else {
                    System.out.println("Failed to request checkout.");
                }
                break;
            case Booking.STATUS_PENDING_CHECKOUT:
                System.out.println("Your checkout request is waiting for admin confirmation.");
                break;
            case Booking.STATUS_READY_FOR_CHECKOUT:
                LocalDate pickupDate = InputUtil.readDate(sc, "Pickup date (yyyy-mm-dd): ");
                if (BookingService.pickupVehicle(booking.getBookingId(), pickupDate)) {
                    System.out.println("Vehicle picked up. Status set to CheckedOut.");
                } else {
                    System.out.println("Failed to pickup vehicle.");
                }
                break;
            case Booking.STATUS_CHECKED_OUT:
                System.out.println("Booking already checked out.");
                break;
            case Booking.STATUS_PENDING_APPROVAL:
                System.out.println("Booking still pending approval.");
                break;
            case Booking.STATUS_RETURNED:
                System.out.println("Booking already returned.");
                break;
            default:
                System.out.println("Cannot checkout in current status: " + booking.getStatus());
        }
    }

    private static void requestReturn(User user) {
        Booking booking = selectMyBookingOrBack(user, "Enter Booking ID to request return (0 to go back): ");
        if (booking == null) return;
        if (BookingService.requestReturn(booking.getBookingId())) {
            System.out.println("Return requested. Waiting for admin confirmation.");
        } else {
            System.out.println("Return request failed. Current status: " + booking.getStatus());
        }
    }

    private static void cancelBooking(User user) {
        Booking booking = selectMyBookingOrBack(user, "Enter Booking ID to cancel (0 to go back): ");
        if (booking == null) return;
        if (BookingService.cancelBooking(booking.getBookingId())) System.out.println("Booking cancelled.");
        else System.out.println("Cannot cancel booking now.");
    }

    private static Booking selectMyBookingOrBack(User user, String prompt) {
        Booking[] bookings = BookingService.listByCustomer(user.getUsername());

        if (bookings.length == 0) {
            System.out.println("No bookings found. Enter 0 to return.");
            while (true) {
                String back = sc.nextLine().trim();
                if (back.equals("0") || back.isEmpty()) return null;
                System.out.println("Enter 0 to return.");
            }
        }

        while (true) {
            viewMyBookings(user);
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equals("0")) return null;

            Booking booking = BookingService.getBookingById(input);
            if (booking != null && booking.getCustomerId().equalsIgnoreCase(user.getUsername())) {
                return booking;
            }

            System.out.println("Invalid Booking ID. Enter 0 to return.");
        }
    }

    private static String[] splitCsv(String csv) {
        String[] raw = csv.split(",");
        String[] out = new String[0];
        for (int i = 0; i < raw.length; i++) {
            String t = raw[i].trim();
            if (!t.isEmpty()) {
                String[] next = new String[out.length + 1];
                for (int j = 0; j < out.length; j++) next[j] = out[j];
                next[out.length] = t;
                out = next;
            }
        }
        return out;
    }
}
