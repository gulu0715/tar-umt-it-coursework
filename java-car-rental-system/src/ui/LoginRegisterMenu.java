package ui;

import useraccount.model.User;
import useraccount.service.AuthService;
import util.InputUtil;
import util.ValidationUtil;

import java.util.Scanner;

public final class LoginRegisterMenu {
    private static final Scanner sc = util.InputUtil.SC;

    private LoginRegisterMenu() {}

    public static void start() {
        int choice;
        do {
            System.out.println("\n=================================");
            System.out.println("        CAR RENTAL SYSTEM");
            System.out.println("=================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            choice = InputUtil.readInt(sc, "Choose: ");
            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerFlow();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);
    }

    private static void loginFlow() {
        String username = InputUtil.readNonEmptyString(sc, "Username: ");
        String password = InputUtil.readNonEmptyString(sc, "Password: ");
        User user = AuthService.login(username, password);
        if (user == null) {
            System.out.println("Login failed.");
            return;
        }
        System.out.println("Login successful.");
        if ("admin".equalsIgnoreCase(user.getRole())) {
            AdminMenu.start(user);
        } else {
            CustomerMenu.start(user);
        }
    }

    private static void registerFlow() {
        String username = InputUtil.readNonEmptyString(sc, "Username: ");
        String password = InputUtil.readNonEmptyString(sc, "Password: ");
        String name = InputUtil.readNonEmptyString(sc, "Full Name: ");

        String phone;
        while (true) {
            phone = InputUtil.readNonEmptyString(sc, "Phone (Malaysia, e.g. 60123456789): ");
            if (ValidationUtil.isValidPhone(phone)) break;
            System.out.println("Invalid phone format. Please enter a Malaysian mobile number starting with 60.");
        }

        String licenseNumber;
        while (true) {
            licenseNumber = InputUtil.readNonEmptyString(sc, "Driving License Number (e.g. D1234567): ");
            if (ValidationUtil.isValidLicenseNumber(licenseNumber)) break;
            System.out.println("Invalid license number format.");
        }

        String licenseType;
        while (true) {
            licenseType = InputUtil.readNonEmptyString(sc, "License Type (AUTO / MANUAL): ");
            if (ValidationUtil.isValidLicenseType(licenseType)) break;
            System.out.println("Invalid license type. Please enter AUTO or MANUAL.");
        }

        boolean ok = AuthService.registerCustomer(username, password, name, phone, licenseNumber, licenseType);
        if (ok) {
            System.out.println("Account and customer profile created.");
        } else {
            System.out.println("Registration failed. Username may already exist or data is invalid.");
        }
    }
}
