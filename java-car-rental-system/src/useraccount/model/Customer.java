package useraccount.model;

public class Customer extends User {
    private String name;
    private String phone;
    private String licenseNumber;
    private String licenseType; // AUTO or MANUAL

    public Customer(String id, String name, String phone, String licenseNumber, String licenseType) {
        super(id, "", "customer");
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
    }

    // Getters
    public String getId() { 
        return getUsername(); 
    }

    public String getName() { 
        return name; 
    }

    public String getPhone() { 
        return phone; 
    }

    public String getLicenseNumber() { 
        return licenseNumber; 
    }

    public String getLicenseType() { 
        return licenseType; 
    }

    // Setters
    public void setName(String name) { 
        this.name = name; 
    }

    public void setPhone(String phone) { 
        this.phone = phone; 
    }

    public void setLicenseNumber(String licenseNumber) { 
        this.licenseNumber = licenseNumber; 
    }

    public void setLicenseType(String licenseType) { 
        this.licenseType = licenseType; 
    }

    @Override
    public String toString() {
        return String.format("%-12s %-18s %-15s %-16s %-10s",
                getId(), name, phone, licenseNumber, licenseType);
    }
}
