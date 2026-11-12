package ui;

import fleetbooking.model.AddOn;
import fleetbooking.model.Booking;
import fleetbooking.model.EconomyCar;
import fleetbooking.model.LuxuryCar;
import fleetbooking.model.SUV;
import fleetbooking.model.Vehicle;
import fleetbooking.service.AddOnService;
import fleetbooking.service.BookingService;
import fleetbooking.service.VehicleService;
import java.time.LocalDate;
import operationsbilling.model.CarInspection;
import operationsbilling.service.InspectionService;
import operationsbilling.service.InvoiceService;
import reportinganalytics.service.ReportService;
import useraccount.model.Customer;
import useraccount.repository.CustomerRepository;
import useraccount.service.AuthService;

public final class Main {
    public static void main(String[] args) {
        AuthService.loadFromFile();
        CustomerRepository.loadFromFile();
        VehicleService.loadFromFile();
        AddOnService.loadFromFile();
        BookingService.loadFromFile();
        InspectionService.loadFromFile();
        InvoiceService.loadFromFile();
        ReportService.loadFromFile();

        if (VehicleService.isEmpty() && CustomerRepository.isEmpty() && AddOnService.isEmpty()) {
            seedDemoData();
        }

        LoginRegisterMenu.start();
        System.out.println("System exited.");
    }

    private static void seedDemoData() {
        System.out.println("[Demo] Loading demo data...");
        System.out.println("=".repeat(60));

        // ==================== 1. REGISTER DEMO USERS ====================
        System.out.println("\n📝 Registering demo users...");
        AuthService.registerDemoUser("alice", "alice123");
        AuthService.registerDemoUser("bob", "bob123");
        AuthService.registerDemoUser("charlie", "charlie123");
        AuthService.registerDemoUser("diana", "diana123");
        AuthService.registerDemoUser("ethan", "ethan123");
        System.out.println("   ✅ Created 5 customer accounts");

        // ==================== 2. ADD CUSTOMER PROFILES ====================
        System.out.println("\n👥 Adding customer profiles...");
        CustomerRepository.add(new Customer("alice", "Alice Tan", "60123456789", "D1234567", "MANUAL"));
        CustomerRepository.add(new Customer("bob", "Bob Lim", "60198765432", "D7654321", "AUTO"));
        CustomerRepository.add(new Customer("charlie", "Charlie Wong", "60112345678", "D2233445", "MANUAL"));
        CustomerRepository.add(new Customer("diana", "Diana Lee", "60139988776", "D9988776", "AUTO"));
        CustomerRepository.add(new Customer("ethan", "Ethan Tan", "60171234567", "D5566778", "MANUAL"));
        System.out.println("   ✅ Added 5 customer profiles");

        // ==================== 3. ADD VEHICLES  ====================
        System.out.println("\n🚗 Adding vehicles...");
        
        // Economy Cars
        Vehicle v1 = new EconomyCar("V001", "Vios", 80.00, 5, "Auto");
        v1.setStock(3);
        v1.setMileage(15000);
        v1.setColor("Silver");
        v1.setYear(2023);
        VehicleService.addDemoVehicle(v1);
        
        Vehicle v3 = new EconomyCar("V003", "Myvi", 90.00, 5, "Manual");
        v3.setStock(2);
        v3.setMileage(8000);
        v3.setColor("Red");
        v3.setYear(2024);
        VehicleService.addDemoVehicle(v3);
        
        Vehicle v6 = new EconomyCar("V006", "City", 85.00, 5, "Auto");
        v6.setStock(2);
        v6.setMileage(12000);
        v6.setColor("Blue");
        v6.setYear(2023);
        VehicleService.addDemoVehicle(v6);
        
        // SUVs
        Vehicle v2 = new SUV("V002", "CR-V", 150.00, 7, "Auto");
        v2.setStock(2);
        v2.setMileage(25000);
        v2.setColor("Black");
        v2.setYear(2022);
        VehicleService.addDemoVehicle(v2);
        
        Vehicle v5 = new SUV("V005", "Hilux", 220.00, 5, "Manual");
        v5.setStock(1);
        v5.setMileage(30000);
        v5.setColor("White");
        v5.setYear(2021);
        VehicleService.addDemoVehicle(v5);
        
        Vehicle v7 = new SUV("V007", "XC90", 280.00, 7, "Auto");
        v7.setStock(1);
        v7.setMileage(5000);
        v7.setColor("Dark Blue");
        v7.setYear(2024);
        VehicleService.addDemoVehicle(v7);
        
        // Luxury Cars
        Vehicle v4 = new LuxuryCar("V004", "E200", 350.00, 5, "Auto");
        v4.setStock(1);
        v4.setMileage(5000);
        v4.setColor("Black");
        v4.setYear(2024);
        VehicleService.addDemoVehicle(v4);
        
        Vehicle v8 = new LuxuryCar("V008", "520i", 380.00, 5, "Auto");
        v8.setStock(1);
        v8.setMileage(3000);
        v8.setColor("White");
        v8.setYear(2024);
        VehicleService.addDemoVehicle(v8);
        
        Vehicle v9 = new LuxuryCar("V009", "A6", 360.00, 5, "Auto");
        v9.setStock(1);
        v9.setMileage(4500);
        v9.setColor("Grey");
        v9.setYear(2023);
        VehicleService.addDemoVehicle(v9);
        
        System.out.println("   ✅ Added 9 vehicles (3 Economy, 3 SUV, 3 Luxury)");

        // ==================== 4. ADD ADD-ONS  ====================
        System.out.println("\n🔧 Adding add-ons...");
        
        AddOnService.addDemoAddOn(new AddOn("A001", "GPS Navigation", 10.00, true, "Navigation", "Turn-by-turn GPS navigation system with real-time traffic"));
        AddOnService.addDemoAddOn(new AddOn("A002", "Insurance Coverage", 20.00, true, "Insurance", "Comprehensive insurance coverage with zero excess"));
        AddOnService.addDemoAddOn(new AddOn("A003", "Baby Car Seat", 15.00, true, "Child Safety", "ISO-FIX compatible baby seat for infants and toddlers"));
        AddOnService.addDemoAddOn(new AddOn("A004", "WiFi Hotspot", 8.00, true, "Connectivity", "Portable 4G WiFi hotspot with unlimited data"));
        AddOnService.addDemoAddOn(new AddOn("A005", "Roof Rack", 25.00, true, "Utility", "Extra cargo space with roof rack and cargo box"));
        AddOnService.addDemoAddOn(new AddOn("A006", "Snow Chains", 12.00, true, "Safety", "Winter driving snow chains for all-weather safety"));
        AddOnService.addDemoAddOn(new AddOn("A007", "Premium Sound", 18.00, true, "Entertainment", "Bose premium sound system upgrade"));
        AddOnService.addDemoAddOn(new AddOn("A008", "Phone Charger", 5.00, true, "Connectivity", "Multi-port USB car charger with fast charging"));
        AddOnService.addDemoAddOn(new AddOn("A009", "Pet Carrier", 12.00, true, "Pet Friendly", "Safe and comfortable pet carrier for travel"));
        AddOnService.addDemoAddOn(new AddOn("A010", "Dash Camera", 9.00, true, "Safety", "Front and rear dash camera with recording"));
        
        System.out.println("   ✅ Added 10 add-ons (Navigation, Insurance, Child Safety, etc.)");

        // ==================== 5. CREATE BOOKINGS ====================
        System.out.println("\n📅 Creating bookings...");
        
        // Booking 1: Alice books Economy Car V001 with GPS and Insurance (COMPLETED)
        Booking b1 = BookingService.createBooking("alice", "V001", 
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5), 
                new String[]{"A001", "A002"});
        if (b1 != null) {
            BookingService.approveBooking(b1.getBookingId());
            BookingService.requestCheckout(b1.getBookingId());
            BookingService.confirmCheckout(b1.getBookingId());
            BookingService.pickupVehicle(b1.getBookingId(), LocalDate.of(2026, 3, 1));
            BookingService.requestReturn(b1.getBookingId());
            BookingService.confirmReturn(b1.getBookingId(), LocalDate.of(2026, 3, 7), true);
            CarInspection ins1 = new CarInspection("V001", b1.getBookingId(), "admin", 
                    CarInspection.FUEL_HALF, CarInspection.DAMAGE_MINOR, 
                    "Small scratch on bumper", false);
            InspectionService.addInspection(ins1);
            System.out.println("   ✅ Booking B0001 (Alice - V001) - COMPLETED");
        }
        
        // Booking 2: Bob books SUV V002 with GPS (COMPLETED)
        Booking b2 = BookingService.createBooking("bob", "V002", 
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 12), 
                new String[]{"A001"});
        if (b2 != null) {
            BookingService.approveBooking(b2.getBookingId());
            BookingService.requestCheckout(b2.getBookingId());
            BookingService.confirmCheckout(b2.getBookingId());
            BookingService.pickupVehicle(b2.getBookingId(), LocalDate.of(2026, 3, 10));
            BookingService.requestReturn(b2.getBookingId());
            BookingService.confirmReturn(b2.getBookingId(), LocalDate.of(2026, 3, 12), true);
            CarInspection ins2 = new CarInspection("V002", b2.getBookingId(), "admin", 
                    CarInspection.FUEL_THREE_QUARTER, CarInspection.DAMAGE_NONE, 
                    "No damage", false);
            InspectionService.addInspection(ins2);
            System.out.println("   ✅ Booking B0002 (Bob - V002) - COMPLETED");
        }
        
        // Booking 3: Charlie books Economy Car V003 (APPROVED only)
        Booking b3 = BookingService.createBooking("charlie", "V003", 
                LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 22), 
                new String[0]);
        if (b3 != null) {
            BookingService.approveBooking(b3.getBookingId());
            System.out.println("   ✅ Booking B0003 (Charlie - V003) - APPROVED");
        }
        
        // Booking 4: Diana books Luxury Car V004 with Insurance and Baby Seat (CHECKED_OUT)
        Booking b4 = BookingService.createBooking("diana", "V004", 
                LocalDate.of(2026, 3, 25), LocalDate.of(2026, 3, 27), 
                new String[]{"A002", "A003"});
        if (b4 != null) {
            BookingService.approveBooking(b4.getBookingId());
            BookingService.requestCheckout(b4.getBookingId());
            BookingService.confirmCheckout(b4.getBookingId());
            System.out.println("   ✅ Booking B0004 (Diana - V004) - CHECKED_OUT");
        }
        
        // Booking 5: Ethan books SUV V005 with Baby Seat (CANCELLED)
        Booking b5 = BookingService.createBooking("ethan", "V005", 
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3), 
                new String[]{"A003"});
        if (b5 != null) {
            BookingService.approveBooking(b5.getBookingId());
            BookingService.cancelBooking(b5.getBookingId());
            System.out.println("   ✅ Booking B0005 (Ethan - V005) - CANCELLED");
        }
        
        // Booking 6: Alice books Luxury Car V008 with Premium Sound (PENDING_APPROVAL)
        Booking b6 = BookingService.createBooking("alice", "V008", 
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 15), 
                new String[]{"A007"});
        if (b6 != null) {
            System.out.println("   ✅ Booking B0006 (Alice - V008) - PENDING_APPROVAL");
        }
        
        // Booking 7: Bob books SUV V007 with Roof Rack and WiFi (PENDING_CHECKOUT)
        Booking b7 = BookingService.createBooking("bob", "V007", 
                LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 8), 
                new String[]{"A004", "A005"});
        if (b7 != null) {
            BookingService.approveBooking(b7.getBookingId());
            BookingService.requestCheckout(b7.getBookingId());
            System.out.println("   ✅ Booking B0007 (Bob - V007) - PENDING_CHECKOUT");
        }

        // ==================== 6. GENERATE INVOICES FOR COMPLETED BOOKINGS ====================
        System.out.println("\n💰 Generating invoices...");
        InvoiceService.generateInvoice(b1.getBookingId());
        InvoiceService.generateInvoice(b2.getBookingId());
        System.out.println("   ✅ Generated invoices for B0001 and B0002");

        // ==================== 7. SAVE ALL DATA TO FILES ====================
        System.out.println("\n💾 Saving data to files...");
        VehicleService.saveToFile();
        CustomerRepository.saveToFile();
        AuthService.saveToFile();
        AddOnService.saveToFile();
        BookingService.saveToFile();
        InspectionService.saveToFile();
        
        // ==================== 8. DISPLAY SUMMARY ====================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    DEMO DATA LOADED SUCCESSFULLY");
        System.out.println("=".repeat(60));
        System.out.println("\n📊 Summary:");
        System.out.println("   • 5 Customer accounts");
        System.out.println("   • 9 Vehicles (3 Economy, 3 SUV, 3 Luxury)");
        System.out.println("   • 10 Add-ons (Navigation, Insurance, Baby Seat, WiFi, etc.)");
        System.out.println("   • 7 Bookings (2 Completed, 1 Approved, 1 Checked Out, 1 Cancelled, 1 Pending, 1 Pending Checkout)");
        System.out.println("   • 2 Invoices generated");
        
    }
}