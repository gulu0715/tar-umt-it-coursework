package fleetbooking.model;

public class SUV extends Vehicle {
    
    public SUV(String carID, String model, double dailyRate, int seatingCapacity, String transmission) {
        super(carID, model, dailyRate, seatingCapacity, transmission);
        setVehicleType("SUV");
        setEngineType("V6 / Inline-4 Turbo");
        setEngineCapacity(2.5);
        setFuelType("Petrol / Diesel");
        setBrand("Honda/Toyota/Mazda/Hyundai");
    }

    // Overrided Getters
    @Override // Medium insurance
    public double getInsuranceRate(){ 
        return 0.10; 
    } 

    @Override // Medium deposit
    public double getDepositRate(){ 
        return 0.20; 
    } 

    @Override // Requires inspection
    public boolean requiresInspection(){ 
        return true; 
    } 

    @Override // Medium cleaning fee
    public double getCleaningFee(){ 
        return 25.00; 
    } 
    
    @Override
    public String getEngineSpecification() {
        return "2.5L V6 / 2.0L Turbocharged 4-Cylinder";
    }
    
    @Override
    public String getSpecialFeatures() {
        return "Spacious Interior, 4WD Capability, High Ground Clearance, Roof Rails, Towing Capacity";
    }
    
    @Override // km per liter - medium fuel efficiency
    public double getFuelEfficiency() {
        return 12.5; 
    }
    
    @Override // SUVs have 4WD/AWD
    public boolean hasFourWheelDrive() {
        return true; 
    }
    
    @Override
    public String getTargetAudience() {
        return "Families, outdoor enthusiasts, drivers needing extra space and capability";
    }
}