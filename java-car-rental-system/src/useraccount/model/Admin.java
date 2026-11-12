package useraccount.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Admin extends User {
    
    // Additional attributes for Admin
    private String employeeId;
    private String department;
    private String position;
    private LocalDateTime lastLogin;
    private String[] permissions;
    private int loginCount;
    private boolean isSuperAdmin;
    
    // Constructor
    public Admin(String username, String password) {
        super(username, password, "admin");
        this.employeeId = generateEmployeeId();
        this.department = "Administration";
        this.position = "System Administrator";
        this.lastLogin = LocalDateTime.now();
        this.permissions = getAllPermissions();
        this.loginCount = 0;
        this.isSuperAdmin = false;
    }
    
    // Overloaded constructor 
    public Admin(String username, String password, String department, String position, boolean isSuperAdmin) {
        super(username, password, "admin");
        this.employeeId = generateEmployeeId();
        this.department = department;
        this.position = position;
        this.lastLogin = LocalDateTime.now();
        this.permissions = isSuperAdmin ? getAllPermissions() : getBasicPermissions();
        this.loginCount = 0;
        this.isSuperAdmin = isSuperAdmin;
    }
    
    private String generateEmployeeId() {
        return "ADM" + System.currentTimeMillis() % 10000;
    }
    
    private String[] getAllPermissions() {
        return new String[]{
            "MANAGE_USERS", "MANAGE_VEHICLES", "MANAGE_BOOKINGS", 
            "MANAGE_INSPECTIONS", "MANAGE_INVOICES", "VIEW_REPORTS", 
            "GENERATE_REPORTS", "DELETE_DATA", "SYSTEM_CONFIG"
        };
    }
    
    private String[] getBasicPermissions() {
        return new String[]{
            "MANAGE_BOOKINGS", "VIEW_REPORTS", "GENERATE_REPORTS"
        };
    }
    
    // Admin-specific methods
    public boolean hasPermission(String permission) {
        for (String p : permissions) {
            if (p.equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }
    
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
        this.loginCount++;
    }
    
    public String getLastLoginFormatted() {
        return lastLogin.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public boolean canDeleteData() {
        return hasPermission("DELETE_DATA");
    }
    
    public boolean isSystemAdmin() {
        return isSuperAdmin || "System Administrator".equals(position);
    }
    
    // Getters
    public String getEmployeeId() { 
        return employeeId; 
    }

    public String getDepartment() { 
        return department; 
    }

    public String getPosition() { 
        return position; 
    }

    public LocalDateTime getLastLogin() { 
        return lastLogin; 
    }

    public String[] getPermissions() { 
        return Arrays.copyOf(permissions, permissions.length); 
    }

    public int getLoginCount() { 
        return loginCount; 
    }

    public boolean isSuperAdmin() { 
        return isSuperAdmin; 
    }
    
    // Setters
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }
    public void setSuperAdmin(boolean superAdmin) { 
        this.isSuperAdmin = superAdmin; 
        this.permissions = superAdmin ? getAllPermissions() : getBasicPermissions();
    }
    
    @Override
    public String toString() {
        return String.format("%-15s %-15s %-10s %-10s %-20s %-10s", 
                getUsername(), getPassword(), getRole(), employeeId, position, 
                isSuperAdmin ? "Yes" : "No");
    }
    
    public String toDetailedString() {
        return String.format(
            "\n┌─────────────────────────────────────────┐\n" +
            "│           ADMIN DETAILS                 │\n" +
            "├─────────────────────────────────────────┤\n" +
            "│ Username    : %-25s │\n" +
            "│ Employee ID : %-25s │\n" +
            "│ Department  : %-25s │\n" +
            "│ Position    : %-25s │\n" +
            "│ Last Login  : %-25s │\n" +
            "│ Login Count : %-25d │\n" +
            "│ Super Admin : %-25s │\n" +
            "│ Permissions : %-25d │\n" +
            "└─────────────────────────────────────────┘",
            getUsername(), employeeId, department, position, 
            getLastLoginFormatted(), loginCount, isSuperAdmin ? "Yes" : "No",
            permissions.length
        );
    }
}