package useraccount.service;

import useraccount.model.Admin;
import useraccount.model.Customer;
import useraccount.model.User;
import useraccount.repository.CustomerRepository;
import util.AppPaths;
import util.FileUtil;
import util.ValidationUtil;

public final class AuthService {
    private static User[] users = new User[20];
    private static int size = 0;
    private static boolean loaded = false;

    private AuthService() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        
        String[] lines = FileUtil.readAllLines(AppPaths.USERS_FILE);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 3) continue;
            
            // Check if this is an admin with extended fields
            if (p.length >= 4 && "admin".equalsIgnoreCase(p[2])) {
                // Load admin with extended attributes
                Admin admin = new Admin(p[0], p[1]);
                if (p.length > 3 && !p[3].isEmpty()) admin.setDepartment(p[3]);
                if (p.length > 4 && !p[4].isEmpty()) admin.setPosition(p[4]);
                if (p.length > 5 && !p[5].isEmpty()) admin.setSuperAdmin(Boolean.parseBoolean(p[5]));
                users = addInternal(users, admin);
            } else {
                users = addInternal(users, new User(p[0], p[1], p[2]));
            }
            size++;
        }
        
        // Create default admin if not exists
        if (getUserByUsername("admin") == null) {
            Admin defaultAdmin = new Admin("admin", "admin", "Administration", "System Administrator", true);
            users = addInternal(users, defaultAdmin);
            size++;
            saveToFile();
            System.out.println("[AuthService] Default admin account created.");
        }
        
        loaded = true;
        System.out.println("[AuthService] Loaded " + size + " users from file.");
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) {
            User u = users[i];
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                lines[i] = String.join("|", 
                    a.getUsername(), 
                    a.getPassword(), 
                    a.getRole(),
                    a.getDepartment(),
                    a.getPosition(),
                    String.valueOf(a.isSuperAdmin())
                );
            } else {
                lines[i] = String.join("|", u.getUsername(), u.getPassword(), u.getRole());
            }
        }
        FileUtil.writeAllLines(AppPaths.USERS_FILE, lines);
    }

    public static User[] listAll() {
        User[] out = new User[size];
        for (int i = 0; i < size; i++) out[i] = users[i];
        return out;
    }

    public static User login(String username, String password) {
        if (username == null || password == null) return null;
        String target = username.trim();
        for (int i = 0; i < size; i++) {
            User u = users[i];
            if (u.getUsername().equalsIgnoreCase(target) && u.getPassword().equals(password)) {
                // Record login time for admin users
                if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    a.recordLogin();
                    saveToFile();
                }
                return u;
            }
        }
        return null;
    }

    public static boolean registerCustomer(String username, String password, String name, String phone,
                                           String licenseNumber, String licenseType) {
        if (username == null || password == null || name == null) return false;
        username = username.trim();
        password = password.trim();
        name = name.trim();
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) return false;
        if (getUserByUsername(username) != null) return false;
        if (CustomerRepository.getById(username) != null) return false;
        if (!ValidationUtil.isValidPhone(phone)) return false;
        if (!ValidationUtil.isValidLicenseNumber(licenseNumber)) return false;
        if (!ValidationUtil.isValidLicenseType(licenseType)) return false;

        String phoneNorm = ValidationUtil.normalizePhone(phone);
        String licNorm = ValidationUtil.normalizeLicenseNumber(licenseNumber);
        String typeNorm = ValidationUtil.normalizeLicenseType(licenseType);

        users = addInternal(users, new User(username, password, "customer"));
        size++;
        CustomerRepository.add(new Customer(username, name, phoneNorm, licNorm, typeNorm));
        saveToFile();
        return true;
    }

    public static boolean registerDemoUser(String username, String password) {
        if (getUserByUsername(username) != null) return false;
        users = addInternal(users, new User(username, password, "customer"));
        size++;
        saveToFile();
        return true;
    }

    public static User getUserByUsername(String username) {
        if (username == null) return null;
        String target = username.trim();
        for (int i = 0; i < size; i++) {
            if (users[i].getUsername().equalsIgnoreCase(target)) return users[i];
        }
        return null;
    }

    public static boolean deleteUserByUsername(String username) {
        if (username == null) return false;
        String target = username.trim();
        int idx = -1;
        for (int i = 0; i < size; i++) {
            if (users[i].getUsername().equalsIgnoreCase(target)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) return false;
        
        // Prevent deletion of the main admin account
        if ("admin".equalsIgnoreCase(users[idx].getUsername())) {
            System.out.println("Cannot delete the main admin account.");
            return false;
        }

        for (int i = idx; i < size - 1; i++) users[i] = users[i + 1];
        users[size - 1] = null;
        size--;
        saveToFile();
        return true;
    }

    public static boolean isEmpty() { 
        return size == 0; 
    }
    
    public static Admin getAdminByUsername(String username) {
        User u = getUserByUsername(username);
        if (u instanceof Admin) {
            return (Admin) u;
        }
        return null;
    }

    private static User[] addInternal(User[] arr, User u) {
        if (size >= arr.length) {
            User[] next = new User[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = u;
        return arr;
    }
}