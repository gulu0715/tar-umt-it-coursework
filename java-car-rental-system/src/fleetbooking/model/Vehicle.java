package fleetbooking.model;

public abstract class Vehicle {
    private String carID;
    private String model;
    private double dailyRate;
    private int seatingCapacity;
    private String transmission;
    private int mileage;
    private boolean maintenanceStatus;
    private int stock;
    private String vehicleType;
    private String engineType;
    private String fuelType;
    private double engineCapacity;
    private String color;
    private int year;
    private String brand;

    // Contructors
    public Vehicle(String carID, String model, double dailyRate, int seatingCapacity, String transmission) {
        this.carID = carID;
        this.model = model;
        this.dailyRate = dailyRate;
        this.seatingCapacity = seatingCapacity;
        this.transmission = transmission;
        this.vehicleType = "Unknown";
        this.mileage = 0;
        this.maintenanceStatus = false;
        this.stock = 0;
        this.engineType = "Standard";
        this.fuelType = "Petrol";
        this.engineCapacity = 1.6;
        this.color = "White";
        this.year = 2024;
        this.brand = "Generic";
    }

    // Getters and Setters
    public String getCarID(){ 
        return carID; 
    }

    public String getModel() { 
        return model; 
    }

    public void setModel(String model) { 
        this.model = model; 
    }

    public double getDailyRate() { 
        return dailyRate; 
    }

    public void setDailyRate(double dailyRate) { 
        this.dailyRate = dailyRate; 
    }

    public int getSeatingCapacity() { 
        return seatingCapacity; 
    }

    public void setSeatingCapacity(int seatingCapacity) { 
        this.seatingCapacity = seatingCapacity; 
    }

    public String getTransmission() { 
        return transmission; 
    }

    public void setTransmission(String transmission) { 
        this.transmission = transmission; 
    }

    public int getMileage() { 
        return mileage; 
    }

    public void setMileage(int mileage) { 
        this.mileage = Math.max(0, mileage); 
    }

    public boolean isMaintenanceStatus() { 
        return maintenanceStatus; 
    }

    public void setMaintenanceStatus(boolean maintenanceStatus) { 
        this.maintenanceStatus = maintenanceStatus; 
    }

    public int getStock() { 
        return stock; 
    }

    public void setStock(int stock) { 
        this.stock = Math.max(0, stock); 
    }

    public String getVehicleType() { 
        return vehicleType; 
    }

    protected void setVehicleType(String vehicleType) { 
        this.vehicleType = vehicleType; 
    }

    public String getEngineType() { 
        return engineType; 
    }

    public void setEngineType(String engineType) { 
        this.engineType = engineType; 
    }

    public String getFuelType() { 
        return fuelType; 
    }

    public void setFuelType(String fuelType) { 
        this.fuelType = fuelType; 
    }

    public double getEngineCapacity() { 
        return engineCapacity; 
    }

    public void setEngineCapacity(double engineCapacity) { 
        this.engineCapacity = engineCapacity; 
    }

    public String getColor() { 
        return color; 
    }

    public void setColor(String color) { 
        this.color = color; 
    }

    public int getYear() { 
        return year; 
    }

    public void setYear(int year) { 
        this.year = year; 
    }

    public String getBrand() { 
        return brand; 
    }

    public void setBrand(String brand) { 
        this.brand = brand; 
    }

    // Abstract methods that implemented by subclasses 
    public abstract double getInsuranceRate();
    public abstract double getDepositRate();
    public abstract boolean requiresInspection();
    public abstract double getCleaningFee();
    
    // Abstract methods for vehicle types
    public abstract String getEngineSpecification();
    public abstract String getSpecialFeatures();
    public abstract double getFuelEfficiency(); // km per liter
    public abstract boolean hasFourWheelDrive();
    public abstract String getTargetAudience();

    public double calculateInsurance() { return calculateInsurance(1); }
    public double calculateInsurance(long bookedDays) {
        long days = Math.max(1, bookedDays);
        return dailyRate * days * getInsuranceRate();
    }
    public double calculateDeposit() { return dailyRate * getDepositRate(); }

    @Override
    public String toString() {
        return String.format("%-8s %-22s %-10s %-10s %-5d %-8d %-8d %-10s RM%-8.2f",
                carID, model, getVehicleType(), transmission, seatingCapacity, mileage, stock,
                maintenanceStatus ? "Yes" : "No", dailyRate);
    }
    
    public String toDetailedString() {
        return String.format("%-8s %-15s %-10s %-8s %-5d %-6.1fL %-8s %-10s RM%-8.2f %-12s %-30s",
                carID, brand, model, vehicleType, year, engineCapacity, fuelType, transmission, 
                dailyRate, getFuelEfficiency() + "km/L", getSpecialFeatures());
    }
}