package ui;

import fleetbooking.model.AddOn;
import fleetbooking.model.Booking;
import fleetbooking.model.Vehicle;
import fleetbooking.service.AddOnService;
import fleetbooking.service.BookingService;
import fleetbooking.service.VehicleService;
import java.time.LocalDate;
import java.util.Scanner;
import operationsbilling.model.CarInspection;
import operationsbilling.model.Invoice;
import operationsbilling.service.InspectionService;
import operationsbilling.service.InvoiceService;
import reportinganalytics.model.Report;
import reportinganalytics.service.ReportService;
import useraccount.model.Admin;
import useraccount.model.Customer;
import useraccount.model.User;
import useraccount.repository.CustomerRepository;
import util.InputUtil;
import util.ValidationUtil;

public final class AdminMenu {
    private static final Scanner sc = util.InputUtil.SC;

    private AdminMenu() {}

    public static void start(User user) {
        // Show admin info on login 
        showAdminInfo(user);
        
        int choice;
        do {
            System.out.println("\n===== ADMIN MODULES =====");
            System.out.println("1. Fleet Module");
            System.out.println("2. Customer Module");
            System.out.println("3. Rental Module");
            System.out.println("4. Report Module");
            System.out.println("5. Admin Profile");
            System.out.println("6. Logout");
            choice = InputUtil.readInt(sc, "Choose: ");

            switch (choice) {
                case 1: 
                    startFleetModule(); 
                    break;
                case 2:
                    startCustomerModule(); 
                    break;
                case 3:
                    startRentalModule(user); 
                    break;
                case 4:
                    reportMenu(); 
                    break;
                case 5: 
                    showAdminProfile(user); 
                    break;
                case 6:
                    System.out.println("Logout successful."); 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 6);
    }
    
    // ==================== ADMIN PROFILE METHODS ====================
    
    private static void showAdminInfo(User user) {
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            System.out.println(admin.toDetailedString());
        } else {
            System.out.println("\n┌─────────────────────────────────────────┐");
            System.out.printf("│ Welcome: %-37s │%n", user.getUsername());
            System.out.printf("│ Role: %-39s │%n", user.getRole());
            System.out.println("└─────────────────────────────────────────┘");
        }
    }
    
    private static void showAdminProfile(User user) {
        if (!(user instanceof Admin)) {
            System.out.println("This feature is only available for admin users.");
            return;
        }
        
        Admin admin = (Admin) user;
        int choice;
        do {
            System.out.println("\n===== ADMIN PROFILE =====");
            System.out.println("1. View Profile Details");
            System.out.println("2. Update Department");
            System.out.println("3. Update Position");
            System.out.println("4. View Permissions");
            System.out.println("5. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            
            switch (choice) {
                case 1:
                    System.out.println(admin.toDetailedString());
                    break;
                case 2:
                    String newDept = InputUtil.readOptionalString(sc, "New Department [" + admin.getDepartment() + "]: ");
                    if (!newDept.isBlank()) {
                        admin.setDepartment(newDept);
                        System.out.println("Department updated successfully.");
                    }
                    break;
                case 3:
                    String newPos = InputUtil.readOptionalString(sc, "New Position [" + admin.getPosition() + "]: ");
                    if (!newPos.isBlank()) {
                        admin.setPosition(newPos);
                        System.out.println("Position updated successfully.");
                    }
                    break;
                case 4:
                    viewPermissions(admin);
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }
    
    private static void viewPermissions(Admin admin) {
        String[] permissions = admin.getPermissions();
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.printf("│ %-47s │%n", "PERMISSIONS FOR: " + admin.getUsername());
        System.out.println("├─────────────────────────────────────────────────┤");        
        for (String p : permissions) {
            System.out.printf("│  ✓ %-44s │%n", p);
        }
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println("\nTotal Permissions: " + permissions.length);
        System.out.println("Super Admin: " + (admin.isSuperAdmin() ? "Yes" : "No"));
    }

    static void startCustomerModule() {
        int choice;
        do {
            System.out.println("\n===== CUSTOMER MANAGEMENT =====");
            System.out.println("1. View Customers");
            System.out.println("2. Update Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    viewCustomers(); 
                    break;
                case 2: 
                    updateCustomer(); 
                    break;
                case 3: 
                    deleteCustomer(); 
                    break;
                case 4: 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    private static void startFleetModule() {
        int choice;
        do {
            System.out.println("\n===== FLEET MANAGEMENT =====");
            System.out.println("1. Vehicle Management");
            System.out.println("2. Add-On Management");
            System.out.println("3. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    vehicleMenu(); 
                    break;
                case 2: 
                    addonMenu(); 
                    break;
                case 3: 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);
    }

    private static void startRentalModule(User user) {
        int choice;
        do {
            System.out.println("\n===== RENTAL MANAGEMENT =====");
            System.out.println("1. Booking Management");
            System.out.println("2. Inspection Management");
            System.out.println("3. Invoice Management");
            System.out.println("4. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    bookingMenu(); 
                    break;
                case 2: 
                    inspectionMenu(user); 
                    break;
                case 3: 
                    invoiceMenu(); 
                    break;
                case 4: 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    private static void viewVehicles() {
        System.out.println("=".repeat(100));
        System.out.println(String.format("%-8s %-22s %-10s %-10s %-5s %-8s %-8s %-10s %-10s", "VEH-ID", "MODEL", "TYPE", "TRANS", "SEATS", "MILE", "STOCK", "MAINT","Price"));
        System.out.println("=".repeat(100));
        for (Vehicle v : VehicleService.listAll()) System.out.println(v);
        System.out.println("=".repeat(100));
    }

    private static void addVehicle() {
        String type = InputUtil.readValidatedString(sc, "Type (Economy/SUV/Luxury): ",
                ValidationUtil::isValidVehicleType, "Please enter Economy, SUV, or Luxury.");
        String model = InputUtil.readNonEmptyString(sc, "Model: ");
        double rate = InputUtil.readPositiveDouble(sc, "Daily Rate: RM");
        int seats = InputUtil.readPositiveInt(sc, "Seating Capacity: ");
        String trans = InputUtil.readValidatedString(sc, "Transmission (Auto/Manual): ",
                ValidationUtil::isValidTransmission, "Please enter Auto or Manual.");
        int mileage = InputUtil.readNonNegativeInt(sc, "Mileage: ");
        int stock = InputUtil.readNonNegativeInt(sc, "Stock: ");
        String id = VehicleService.nextVehicleId();
        Vehicle v = VehicleService.createVehicle(type, id, model, rate, seats, trans);
        
        if (v == null) {
            System.out.println("Invalid vehicle type or transmission.");
            return;
        }

        v.setMileage(mileage);
        v.setStock(stock);

        if (VehicleService.add(v)){ 
            System.out.println("Vehicle added: " + id);
        } else {
            System.out.println("Failed to add vehicle.");
        }
    }

    private static void updateVehicle() {
        viewVehicles();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Vehicle ID to update (0 to go back): ",
                s -> VehicleService.getVehicleById(s) != null, "Vehicle not found. Please enter a valid Vehicle ID.");
        if (id == null) return;
        Vehicle v = VehicleService.getVehicleById(id);
        if (v == null) {
            System.out.println("Vehicle not found.");
            return;
        }

        String model = InputUtil.readOptionalString(sc, "Model [" + v.getModel() + "]: ");
        Double rate = InputUtil.readOptionalPositiveDouble(sc, "Daily Rate [" + v.getDailyRate() + "]: ");
        Integer seats = InputUtil.readOptionalPositiveInt(sc, "Seats [" + v.getSeatingCapacity() + "]: ");
        String trans = InputUtil.readOptionalValidatedString(sc, "Transmission [" + v.getTransmission() + "]: ",
                ValidationUtil::isValidTransmission, "Please enter Auto or Manual, or press Enter to keep current value.");
        Integer mileage = InputUtil.readOptionalNonNegativeInt(sc, "Mileage [" + v.getMileage() + "]: ");
        Integer stock = InputUtil.readOptionalNonNegativeInt(sc, "Stock [" + v.getStock() + "]: ");
        String maint = InputUtil.readOptionalValidatedString(sc, "Maintenance (Y/N) [" + (v.isMaintenanceStatus() ? "Y" : "N") + "]: ",
                s -> s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("N"), "Please enter Y or N, or press Enter to keep current value.");

        if (!model.isBlank()) v.setModel(model);
        if (rate != null) v.setDailyRate(rate);
        if (seats != null) v.setSeatingCapacity(seats);
        if (!trans.isBlank()) v.setTransmission(ValidationUtil.normalizeTransmission(trans));
        if (mileage != null) v.setMileage(mileage);
        if (stock != null) v.setStock(stock);
        if (!maint.isBlank()) v.setMaintenanceStatus(maint.equalsIgnoreCase("Y"));
        VehicleService.saveToFile();
        System.out.println("Vehicle updated.");
    }

    private static void deleteVehicle() {
        viewVehicles();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Vehicle ID to delete (0 to go back): ",
                s -> VehicleService.getVehicleById(s) != null, "Vehicle not found. Please enter a valid Vehicle ID.");
        if (id == null) return;
        if (VehicleService.deleteById(id)) System.out.println("Vehicle removed.");
        else System.out.println("Vehicle not found.");
    }

    private static void vehicleMenu() {
        int choice;
        do {
            System.out.println("\n===== VEHICLE MANAGEMENT =====");
            System.out.println("1. View Vehicles");
            System.out.println("2. Add Vehicle");
            System.out.println("3. Update Vehicle");
            System.out.println("4. Delete Vehicle");
            System.out.println("5. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    viewVehicles(); 
                    break;
                case 2: 
                    addVehicle(); 
                    break;
                case 3: 
                    updateVehicle(); 
                    break;
                case 4: 
                    deleteVehicle(); 
                    break;
                case 5: 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    private static void viewAddOns() {
        System.out.println("=".repeat(70));
        System.out.println(String.format("%-8s %-20s %-12s %-8s %-15s", "ADD-ID", "NAME", "PRICE/DAY", "ACTIVE", "CATEGORY"));
        System.out.println("=".repeat(70));
        for (AddOn a : AddOnService.listAll()) System.out.println(a);
        System.out.println("=".repeat(70));
    }

    private static void addAddOn() {
        String name = InputUtil.readNonEmptyString(sc, "Add-On Name: ");
        double price = InputUtil.readPositiveDouble(sc, "Price per day: RM");
        String category = InputUtil.readOptionalString(sc, "Category (Navigation/Insurance/Child Safety/Connectivity/Utility/Safety/General): ");
        if (category.isBlank()) category = "General";
        String description = InputUtil.readOptionalString(sc, "Description: ");
        String id = AddOnService.nextId();
        if (AddOnService.add(new AddOn(id, name, price, true, category, description))) {
            System.out.println("Add-On added: " + id);
        } else {
            System.out.println("Failed to add add-on.");
        }
    }

    private static void updateAddOn() {
        viewAddOns();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Add-On ID to update (0 to go back): ",
                s -> AddOnService.getById(s) != null, "Add-On not found. Please enter a valid Add-On ID.");
        if (id == null) return;
        AddOn old = AddOnService.getById(id);
        if (old == null) {
            System.out.println("Add-On not found.");
            return;
        }
        String name = InputUtil.readOptionalString(sc, "Name [" + old.getName() + "]: ");
        Double price = InputUtil.readOptionalPositiveDouble(sc, "Price [" + old.getPricePerDay() + "]: ");
        String active = InputUtil.readOptionalValidatedString(sc, "Active (Y/N) [" + (old.isActive() ? "Y" : "N") + "]: ",
                s -> s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("N"), "Please enter Y or N, or press Enter to keep current value.");
        String category = InputUtil.readOptionalString(sc, "Category [" + old.getCategory() + "]: ");
        String description = InputUtil.readOptionalString(sc, "Description [" + old.getDescription() + "]: ");
        
        if (!name.isBlank()) old.setName(name);
        if (price != null) old.setPricePerDay(price);
        if (!active.isBlank()) old.setActive(active.equalsIgnoreCase("Y"));
        if (!category.isBlank()) old.setCategory(category);
        if (!description.isBlank()) old.setDescription(description);
        
        AddOnService.saveToFile();
        System.out.println("Add-On updated.");
    }

    private static void deleteAddOn() {
        viewAddOns();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Add-On ID to delete (0 to go back): ",
                s -> AddOnService.getById(s) != null, "Add-On not found. Please enter a valid Add-On ID.");
        if (id == null) return;
        if (AddOnService.deleteById(id)) System.out.println("Add-On deleted.");
        else System.out.println("Add-On not found.");
    }

    private static void addonMenu() {
        int choice;
        do {
            System.out.println("\n===== ADD-ON MANAGEMENT =====");
            System.out.println("1. View Add-Ons");
            System.out.println("2. Add Add-On");
            System.out.println("3. Update Add-On");
            System.out.println("4. Delete Add-On");
            System.out.println("5. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1: 
                    viewAddOns(); 
                    break;
                case 2: 
                    addAddOn(); 
                    break;
                case 3: 
                    updateAddOn(); 
                    break;
                case 4: 
                    deleteAddOn(); 
                    break;
                case 5: 
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    private static void viewCustomers() {
        Customer[] customers = CustomerRepository.listAll();
        System.out.println("=".repeat(75));
        System.out.println(String.format("%-12s %-18s %-15s %-16s %-10s", "ID", "NAME", "PHONE", "LICENSE NO", "TYPE"));
        System.out.println("=".repeat(75));
        if (customers.length == 0) {
            System.out.println("No customers.");
        } else {
            for (Customer c : customers) System.out.println(c);
        }
        System.out.println("=".repeat(75));
    }

    private static void updateCustomer() {
        viewCustomers();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Customer ID to update (0 to go back): ",
                s -> CustomerRepository.getById(s) != null, "Customer not found. Please enter a valid Customer ID.");
        if (id == null) return;
        Customer old = CustomerRepository.getById(id);
        if (old == null) {
            System.out.println("Customer not found.");
            return;
        }

        String name = InputUtil.readOptionalString(sc, "Name [" + old.getName() + "]: ");
        String phone = InputUtil.readOptionalValidatedString(sc, "Phone [" + old.getPhone() + "]: ",
                ValidationUtil::isValidPhone, "Please enter a valid phone number, or press Enter to keep current value.");
        String lic = InputUtil.readOptionalValidatedString(sc, "License No [" + old.getLicenseNumber() + "]: ",
                ValidationUtil::isValidLicenseNumber, "Please enter a valid license number, or press Enter to keep current value.");
        String type = InputUtil.readOptionalValidatedString(sc, "License Type [" + old.getLicenseType() + "]: ",
                ValidationUtil::isValidLicenseType, "Please enter AUTO or MANUAL, or press Enter to keep current value.");

        if (!name.isBlank()) old.setName(name);
        if (!phone.isBlank()) old.setPhone(ValidationUtil.normalizePhone(phone));
        if (!lic.isBlank()) old.setLicenseNumber(ValidationUtil.normalizeLicenseNumber(lic));
        if (!type.isBlank()) old.setLicenseType(ValidationUtil.normalizeLicenseType(type));
        CustomerRepository.saveToFile();
        System.out.println("Customer updated.");
    }

    private static void deleteCustomer() {
        viewCustomers();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Customer ID to delete (0 to go back): ",
                s -> CustomerRepository.getById(s) != null, "Customer not found. Please enter a valid Customer ID.");
        if (id == null) return;
        if (CustomerRepository.deleteById(id)) System.out.println("Customer removed.");
        else System.out.println("Customer not found.");
    }

    private static void bookingMenu() {
        int choice;
        do {
            System.out.println("\n===== BOOKING MANAGEMENT =====");
            System.out.println("1. View Bookings");
            System.out.println("2. Approve Booking");
            System.out.println("3. Reject Booking");
            System.out.println("4. Confirm Checkout");
            System.out.println("5. Confirm Return");
            System.out.println("6. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1:
                    viewBookings();
                    break;
                case 2:
                    viewBookings();
                    String a = InputUtil.readBackableValidatedString(sc, "Booking ID to approve (0 to go back): ",
                            s -> BookingService.getBookingById(s) != null, "Booking not found. Please enter a valid Booking ID.");
                    if (a == null) break;
                    System.out.println(BookingService.approveBooking(a) ? "Approved." : "Failed.");
                    break;
                case 3:
                    viewBookings();
                    String r = InputUtil.readBackableValidatedString(sc, "Booking ID to reject (0 to go back): ",
                            s -> BookingService.getBookingById(s) != null, "Booking not found. Please enter a valid Booking ID.");
                    if (r == null) break;
                    System.out.println(BookingService.rejectBooking(r) ? "Rejected." : "Failed.");
                    break;
                case 4:
                    viewBookings();
                    String c = InputUtil.readBackableValidatedString(sc, "Booking ID to confirm checkout (0 to go back): ",
                            s -> BookingService.getBookingById(s) != null, "Booking not found. Please enter a valid Booking ID.");
                    if (c == null) break;
                    System.out.println(BookingService.confirmCheckout(c) ? "Checkout confirmed." : "Failed.");
                    break;
                case 5:
                    viewBookings();
                    String bid = InputUtil.readBackableValidatedString(sc, "Booking ID to confirm return (0 to go back): ",
                            s -> BookingService.getBookingById(s) != null, "Booking not found. Please enter a valid Booking ID.");
                    if (bid == null) break;
                    LocalDate date = InputUtil.readDate(sc, "Actual return date (yyyy-mm-dd): ");
                    boolean returned = InputUtil.readYesNo(sc, "Has the customer physically returned the car? (Y/N): ");
                    double late = BookingService.confirmReturn(bid, date, returned);
                    if (!Double.isNaN(late)) System.out.println(String.format("Return confirmed. Late fee: RM%.2f%n", late));
                    break;
                case 6: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 6);
    }

    private static void viewBookings() {
        System.out.println("=".repeat(141));
        System.out.println(String.format("%-8s %-12s %-8s %-10s %-10s %-18s %-10s %-10s %-10s %-10s %-10s %-10s",
                "BID", "CUST", "VEH", "START", "END", "STATUS", "BASE(RM)", "ADDON(RM)", "LATE(RM)","Price(RM)","TOTAL(RM)", "ADDONS"));
        System.out.println("=".repeat(141));
        for (Booking b : BookingService.listAll()) System.out.println(b);
        System.out.println("=".repeat(141));
    }

    private static void inspectionMenu(User user) {
        int choice;
        do {
            System.out.println("\n===== INSPECTION MANAGEMENT =====");
            System.out.println("1. Conduct Inspection");
            System.out.println("2. View All Inspections");
            System.out.println("3. Update Inspection");
            System.out.println("4. Delete Inspection");
            System.out.println("5. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1:
                    InspectionService.conductInspection(user.getUsername(), sc);
                    break;
                case 2:
                    InspectionService.viewAllInspections();
                    break;
                case 3:
                    updateInspectionUI();
                    break;
                case 4:
                    deleteInspectionUI();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    private static void updateInspectionUI() {
        InspectionService.viewAllInspections();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Inspection ID to update (0 to go back): ",
                s -> InspectionService.getById(s) != null,
                "Inspection not found.");
        if (id == null) return;
        CarInspection old = InspectionService.getById(id);
        System.out.println("Current values:");
        System.out.println("  Fuel Level: " + old.getFuelLevel());
        System.out.println("  Damage Severity: " + old.getDamageSeverity());
        System.out.println("  Damage Description: " + old.getDamageDescription());
        System.out.println("  Maintenance Required: " + (old.isMaintenanceRequired() ? "Yes" : "No"));

        int fuelChoice = InputUtil.readIntInRange(sc, "New Fuel Level (1 Full, 2 ThreeQuarter, 3 Half, 4 Quarter, 5 Empty): ", 1, 5);
        String fuel = InspectionService.parseFuel(fuelChoice);
        int damageChoice = InputUtil.readIntInRange(sc, "New Damage Severity (1 None, 2 Minor, 3 Moderate, 4 Severe): ", 1, 4);
        String damage = InspectionService.parseDamage(damageChoice);
        String desc = InputUtil.readNonEmptyString(sc, "New Damage Description: ");
        boolean maintenance = InputUtil.readYesNo(sc, "Maintenance Required? (Y/N): ");

        if (InspectionService.updateInspection(id, fuel, damage, desc, maintenance)) {
            System.out.println("Inspection updated.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private static void deleteInspectionUI() {
        InspectionService.viewAllInspections();
        String id = InputUtil.readBackableValidatedString(sc, "Enter Inspection ID to delete (0 to go back): ",
                s -> InspectionService.getById(s) != null,
                "Inspection not found.");
        if (id == null) return;
        if (InputUtil.readYesNo(sc, "Are you sure? (Y/N): ")) {
            if (InspectionService.deleteInspection(id)) {
                System.out.println("Inspection deleted.");
            } else {
                System.out.println("Delete failed.");
            }
        }
    }
    
    private static void invoiceMenu() {
        int choice;
        do {
            System.out.println("\n===== INVOICE MANAGEMENT =====");
            System.out.println("1. Generate Invoice");
            System.out.println("2. View All Invoices");
            System.out.println("3. Reprint Invoice");
            System.out.println("4. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1:
                    viewBookingsForInvoice();
                    String bid = InputUtil.readBackableValidatedString(sc, "Booking ID to generate invoice (0 to go back): ",
                            s -> BookingService.getBookingById(s) != null && BookingService.getBookingById(s).getStatus().equalsIgnoreCase(Booking.STATUS_RETURNED),
                            "Invoice can only be generated for returned bookings. Please enter a valid Booking ID.");
                    if (bid == null) break;
                    Invoice invoice = InvoiceService.generateInvoice(bid);
                    if (invoice != null) System.out.println(invoice.toFormattedString());
                    break;
                case 2:
                    InvoiceService.viewAllInvoices();
                    break;
                case 3:
                    viewInvoices();
                    String invId = InputUtil.readBackableValidatedString(sc, "Enter Invoice ID to reprint (0 to go back): ",
                            s -> InvoiceService.getById(s) != null, "Invoice not found. Please enter a valid Invoice ID.");
                    if (invId == null) break;
                    Invoice inv = InvoiceService.getById(invId);
                    if (inv != null) System.out.println(inv.toFormattedString());
                    else System.out.println("Invoice not found.");
                    break;
                case 4: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    private static void reportMenu() {
        int choice;
        do {
            System.out.println("\n===== REPORT MANAGEMENT =====");
            System.out.println("1. Revenue Report");
            System.out.println("2. Vehicle Usage Report");
            System.out.println("3. Customer Spending Report");
            System.out.println("4. View All Reports");
            System.out.println("5. Update Report");
            System.out.println("6. Delete Report");
            System.out.println("7. Export Report to CSV");
            System.out.println("8. Report Statistics");
            System.out.println("9. Back");
            choice = InputUtil.readInt(sc, "Choose: ");
            
            switch (choice) {
                case 1:
                    System.out.println("\n--- Generate Revenue Report ---");
                    LocalDate start1 = InputUtil.readDate(sc, "Start date (yyyy-mm-dd): ");
                    LocalDate end1;
                    do {
                        end1 = InputUtil.readDate(sc, "End date (yyyy-mm-dd): ");
                        if (end1.isBefore(start1)) {
                            System.out.println("End date cannot be before start date.");
                        }
                    } while (end1.isBefore(start1));
                    ReportService.generateRevenueReport(start1, end1);
                    break;
                    
                case 2:
                    System.out.println("\n--- Generate Vehicle Usage Report ---");
                    LocalDate start2 = InputUtil.readDate(sc, "Start date (yyyy-mm-dd): ");
                    LocalDate end2;
                    do {
                        end2 = InputUtil.readDate(sc, "End date (yyyy-mm-dd): ");
                        if (end2.isBefore(start2)) {
                            System.out.println("End date cannot be before start date.");
                        }
                    } while (end2.isBefore(start2));
                    ReportService.generateVehicleReport(start2, end2);
                    break;
                    
                case 3:
                    System.out.println("\n--- Generate Customer Spending Report ---");
                    LocalDate start3 = InputUtil.readDate(sc, "Start date (yyyy-mm-dd): ");
                    LocalDate end3;
                    do {
                        end3 = InputUtil.readDate(sc, "End date (yyyy-mm-dd): ");
                        if (end3.isBefore(start3)) {
                            System.out.println("End date cannot be before start date.");
                        }
                    } while (end3.isBefore(start3));
                    ReportService.generateCustomerReport(start3, end3);
                    break;
                    
                case 4:
                    ReportService.viewAllReports();
                    break;
                    
                case 5:
                    System.out.println("\n--- Update Report ---");
                    ReportService.viewAllReports();
                    if (ReportService.listAll().length == 0) {
                        System.out.println("No reports to update. Generate a report first.");
                        break;
                    }
                    String rid = InputUtil.readBackableValidatedString(sc, "Enter Report ID to update (0 to go back): ",
                            s -> ReportService.getById(s) != null,
                            "Report not found. Please enter a valid Report ID.");
                    if (rid == null) break;
                    
                    Report oldReport = ReportService.getById(rid);
                    System.out.println("\nCurrent Report Details:");
                    System.out.println("  Type: " + oldReport.getReportType());
                    System.out.println("  Period: " + oldReport.getStartDate() + " → " + oldReport.getEndDate());
                    System.out.println("  Bookings: " + oldReport.getBookingCount());
                    System.out.println("  Revenue: RM " + String.format("%.2f", oldReport.getTotalRevenue()));
                    
                    System.out.println("\nEnter new date range:");
                    LocalDate ns = InputUtil.readDate(sc, "New start date (yyyy-mm-dd): ");
                    LocalDate ne;
                    do {
                        ne = InputUtil.readDate(sc, "New end date (yyyy-mm-dd): ");
                        if (ne.isBefore(ns)) {
                            System.out.println("End date cannot be before start date.");
                        }
                    } while (ne.isBefore(ns));
                    
                    if (ReportService.updateReport(rid, ns, ne)) {
                        System.out.println("\nReport updated. Use Option 4 to view the changes.");
                    } else {
                        System.out.println("Failed to update report.");
                    }
                    break;
                    
                case 6:
                    System.out.println("\n--- Delete Report ---");
                    ReportService.viewAllReports();
                    if (ReportService.listAll().length == 0) {
                        System.out.println("No reports to delete.");
                        break;
                    }
                    String del = InputUtil.readBackableValidatedString(sc, "Enter Report ID to delete (0 to go back): ",
                            s -> ReportService.getById(s) != null,
                            "Report not found. Please enter a valid Report ID.");
                    if (del == null) break;
                    if (InputUtil.readYesNo(sc, "Are you sure you want to delete this report? (Y/N): ")) {
                        ReportService.deleteReport(del);
                    } else {
                        System.out.println("Delete cancelled.");
                    }
                    break;
                    
                case 7:
                    System.out.println("\n--- Export Report to CSV ---");
                    ReportService.viewAllReports();
                    if (ReportService.listAll().length == 0) {
                        System.out.println("No reports to export.");
                        break;
                    }
                    String expId = InputUtil.readBackableValidatedString(sc, "Enter Report ID to export (0 to go back): ",
                            s -> ReportService.getById(s) != null,
                            "Report not found.");
                    if (expId == null) break;
                    ReportService.exportReportToCSV(expId);
                    break;
                    
                case 8:
                    System.out.println("\n--- Report Statistics ---");
                    ReportService.printReportStatistics();
                    break;
                    
                case 9:
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 9);
    }

    private static void viewBookingsForInvoice() {
        System.out.println("=".repeat(141));
        System.out.println(String.format("%-8s %-12s %-8s %-10s %-10s %-18s RM%-8s RM%-8s RM%-8s RM%-8s %-15s%n",
                "BID", "CUST", "VEH", "START", "END", "STATUS", "BASE", "ADDON", "LATE", "TOTAL", "ADDONS"));
        System.out.println("=".repeat(141));
        for (Booking b : BookingService.listAll()) System.out.println(b);
        System.out.println("=".repeat(141));
    }

    private static void viewInvoices() {
        InvoiceService.viewAllInvoices();
    }
}