package fleetbooking.model;

public class EconomyCar extends Vehicle {
    
    public EconomyCar(String carID, String model, double dailyRate, int seatingCapacity, String transmission) {
        super(carID, model, dailyRate, seatingCapacity, transmission);
        setVehicleType("Economy");
        setEngineType("Inline-3");
        setEngineCapacity(1.0);
        setFuelType("Petrol/Electric Hybrid");
        setBrand("Toyota/Perodua/Honda");
    }

    // Overrided Getters
    @Override // Lowest insurance
    public double getInsuranceRate(){ 
        return 0.05; 
    } 

    @Override // Lowest deposit
    public double getDepositRate(){ 
        return 0.10; 
    } 

    @Override // No special inspection needed
    public boolean requiresInspection(){ 
        return false; 
    } 

    @Override // Lowest cleaning fee
    public double getCleaningFee(){ 
        return 10.00; 
    } 

    @Override
    public String getEngineSpecification() {
        return "1.0L 3-Cylinder Turbo / 1.5L Hybrid";
    }
    
    @Override
    public String getSpecialFeatures() {
        return "Fuel Efficient, Low Maintenance, Affordable, Eco-Friendly";
    }
    
    @Override // km per liter - most fuel efficient
    public double getFuelEfficiency() {
        return 22.5; 
    }
    
    @Override // Economy cars are 2WD
    public boolean hasFourWheelDrive() {
        return false; 
    }
    
    @Override
    public String getTargetAudience() {
        return "Budget-conscious individuals, students, first-time drivers";
    }
}